package com.gym.pilatis.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.gym.pilatis.Manager;
import com.gym.pilatis.helper.GymHelper;
import com.gym.pilatis.helper.GymLRU;

public class AccountManager extends Manager {
    private AccountManager() {
    };

    public static final AccountManager INSTANCE = new AccountManager();

    // 2. TODO use GymLRU to support trimming when accessing map
    private final GymLRU<Integer, Member> logInMemberMap = new GymLRU<>();

    public Member getMember(int memberID) {
        if (logInMemberMap.containsKey(memberID)) {
            return logInMemberMap.get(memberID);
        }

        return logInMemberMap.getOrDefault(memberID, Member.EMPTY);
    }

    // TODO register, create session, persist into db, cache <id, member>
    public Member createMemberFromRegistration(LoginModel model) {

        String query = 
                "SELECT * " +
                "  FROM member " +
                " WHERE Email = " + db.prep(model.getEmail());

        if (db.hasRecord(query)) {
            return Member.EMPTY;
        }

        query = 
                "INSERT INTO member       " +
                "   SET Name            = " + db.prep(model.getName()) +
                "     , Email           = " + db.prep(model.getEmail()) +
                "     , Password        = " + db.prep(model.getPassword()) +
                "     , RegisteredTime  = " + db.prep(model.getRegTime().toString()) +
                "     , Type            = " + 1;

        Optional<Integer> memberIDOptional = db.getNewID(query, "MemberID");

        if (!memberIDOptional.isPresent()) {
            // TODO log failing
            return Member.EMPTY;
        }

        return createMemberFromLogin(model);
    }

    public Member createMemberFromLogin(LoginModel model) {
        String query = 
                "SELECT MemberID    " +
                "     , Type        " +
                "     , Name        " +
                "     , Password    " +
                "  FROM member      " +
                " WHERE email     = " + db.prep(model.getEmail()) +
                "   AND Password  = " + db.prep(model.getPassword());


        try  {
            ResultSet memberInfoFromDB = db.run(query);
            if (memberInfoFromDB.next()) {
                String password = memberInfoFromDB.getString("Password");
                int memberID = memberInfoFromDB.getInt("MemberID");

                if (GymHelper.isEmpty(password) || memberID == 0) {
                    return Member.EMPTY;
                }

                if (logInMemberMap.containsKey(memberID)) {
                    return logInMemberMap.get(memberID);
                }

                Set<String> recognizedIPs = new HashSet<>();
                String query2 = 
                        "SELECT IP " +
                        "  FROM member_login_persistent" +
                        " WHERE MemberID = " + memberID;
                ResultSet ipsFromDB = db.run(query2);
                while (ipsFromDB.next()) {
                    recognizedIPs.add(ipsFromDB.getString("IP"));
                }

                return new Member(
                        memberInfoFromDB.getInt("MemberID"), 
                        memberInfoFromDB.getInt("Type"), 
                        model.getEmail(), 
                        memberInfoFromDB.getString("Name"), 
                        recognizedIPs);
            }
            while (memberInfoFromDB.next()) {
            }
   
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Member.EMPTY;
    }

    public void addNewLoginMember(Member member) {
        logInMemberMap.add(member.getMemberID(), member);
    }

    public void removeLoginMember(Member member) {
        logInMemberMap.remove(member.getMemberID());
    }
}
