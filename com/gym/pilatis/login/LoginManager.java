/*
 * Note
 * session to member cache
 * 
 * check sessionKey log
 * check persistentKey log
 * create sessionKey
 * create persistentKey
 * log new device login nad registration
 */
/*
 * Use 2 cookies to manage member log in
 * a. LongLastingKey llgk (persistentKey) encrypt(salt + ID + LastLogInTime). first 11 chars + 1 check sum add up to 0.
 * b. ShortLastingKey slak (sessionKey) encrypt(salt + oaig + LastLogInTime). first 11 chars + 1 check sum add up to 0.
 * 
 * 1. if no slak and llgk, return empty
 * 1. if(slak in cahce) -> member
 * 2. if(llgk) ->
 * 1. find memberID, remove memberID and slak from cache
 * 2. create new member and slak, save in cache
 * 3. if clear cookies, return empty
 * 
 * LastLogInTime means time when create new account or log in from an unknown device
 * Track in table(Member, device, ip, LastLogInTime)
 */

package com.gym.pilatis.login;

import java.util.Optional;
import java.util.Set;

import org.joda.time.DateTime;

import com.gym.pilatis.HTTP;
import com.gym.pilatis.Manager;
import com.gym.pilatis.account.AccountManager;
import com.gym.pilatis.account.Member;
import com.gym.pilatis.helper.DBHelper;
import com.gym.pilatis.helper.DataHelper;
import com.gym.pilatis.helper.GymHelper;
import com.gym.pilatis.helper.GymMultiKeyBiMap;
import com.gym.pilatis.helper.TimeHelper;

/*
 * TODO handle browser disabled cookies, may need to pass third status key
 * TODO add sessionStorage variable in front end
 */

public class LoginManager extends Manager {

    public static final LoginManager INSTANCE = new LoginManager();

    private LoginManager() {
    }

    private static final int EMPTY_MEMBER_ID = Member.EMPTY.getMemberID();
    private final GymMultiKeyBiMap<String, Integer> sessionToMemberIdBiMap = new GymMultiKeyBiMap<>();

    public static final String SESSION_KEY = "slgk";    // short lasting key = session key; expired at client side or evicted by LRU
    public static final String PERSISTENT_KEY = "llgk"; // long lasting key = persistent key; reset at client browser when new log in
    public static final int LOGIN_KEY_LENGTH = 20; // short lasting key = session key

    public void setHttpMember(HTTP http) {
        String sessionKey = http.getShortTermKey();
        String persistentKey = http.getLongTermKey();
        int memberID = getMemberIdFromLoginKeys(persistentKey, sessionKey);

        // When member found by session key or persistent key 
        // Double verify if request comes from member's recognized IP
        Member member = AccountManager.INSTANCE.getMember(memberID);
        
        if (member.isRecognizedIP(http.getIP())) {

            if(GymHelper.isEmpty(sessionKey)) {
                sessionKey = DataHelper.encode(DateTime.now() + http.getLongTermKey(), LOGIN_KEY_LENGTH);
            }
            sessionToMemberIdBiMap.put(sessionKey, memberID);

            http.setCookie(SESSION_KEY, sessionKey, TimeHelper.getSecondsByDay(7));
            
            http.setMember(member);
        } else {
            http.setMember(Member.EMPTY);    
        }
    }

    private int getMemberIdFromLoginKeys(String persistentKey, String sessionKey) {
        if (persistentKey == null /*|| persistentKey.length() != LOGIN_KEY_LENGTH*/) {
            return EMPTY_MEMBER_ID;
        }

        if (sessionKey != null && sessionToMemberIdBiMap.containsKey(sessionKey)) {
            return sessionToMemberIdBiMap.getValue(sessionKey);
        }

        // if session key not in cache, try to find MemberID in db using persistentKey
        String query = "SELECT MemberID FROM member_login_persistent WHERE PersistentKey = " + db.prep(persistentKey);
        
        Optional<Integer> memberID = db.getInt(query);
//        if (memberID.isPresent()) {
//            sessionToMemberIdBiMap.deleteValue(memberID.get());
//        }
        return memberID.orElse(EMPTY_MEMBER_ID);
    }

    /* Set */

    public boolean addNewLoginRecord(Member member, HTTP http, String persistentKey) {

        // USE SPROC
        String query = 
                "INSERT INTO member_login_persistent" + 
                "   SET MemberID        = " + member.getMemberID() + 
                "     , PersistentKey   = " + db.prep(persistentKey) + 
                "     , IP              = " + db.prep(http.getIP()) +
                "    ON DUPLICATE KEY UPDATE " + 
                "       PersistentKey = " + db.prep(persistentKey);
                ;
        boolean success = DBHelper.INSTANCE.runUpdate(query) > 0;
        if (!success) {
            // TODO log and remove from cache
            return false;
        }
        query = 
                "INSERT INTO member_login_history" + 
                "   SET MemberID        = " + member.getMemberID() + 
                "     , Device          = " + db.prep(http.getDevice()) + 
                "     , IP              = " + db.prep(http.getIP()) +
                "     , LoginTime       = " + db.prep(http.getRequestTime());
        DBHelper.INSTANCE.runUpdate(query);

        return true;
    }

    public void addLoginSessionKey(int memberID, String sessionKey) {
        sessionToMemberIdBiMap.put(sessionKey, memberID);
    }
    
    public void removeLoginSessionKey(String key) {
        sessionToMemberIdBiMap.deleteKey(key);
    }
    
    /* Get */

    public Set<String> getLoginSessionKeys(int memberID) {
        return sessionToMemberIdBiMap.getKeySet(memberID);
    }
}
