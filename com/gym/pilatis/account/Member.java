package com.gym.pilatis.account;

import java.util.HashSet;
import java.util.Set;

public class Member {

    public static final Member EMPTY = new Member();

    private Member() {
        this(0, 0, "", "", new HashSet<>());
    }

    public Member(int memberID, int accountType, String email, String name, Set<String> recognizedIPs) {
        this.setMemberID(memberID)
                .setAccountType(accountType)
                .setEmail(email)
                .setName(name)
                .setRecognizedIPs(recognizedIPs);
    }

    private int memberID;
    private int accountType;
    private Set<String> recognizedIPs;

    // OptionalFields, 
    private String email;
    private String name;

    // type
    // course
    // purchase history

    // Optional fields

    /* Set Properties*/
    private Member setMemberID(int memberID) {
        this.memberID = memberID;
        return this;
    }

    private Member setAccountType(int accountType) {
        this.accountType = accountType;
        return this;
    }

    private Member setEmail(String email) {
        this.email = email;
        return this;
    }

    private Member setName(String name) {
        this.name = name;
        return this;
    }

    private Member setRecognizedIPs(Set<String> recognizedIPs) {
        this.recognizedIPs = recognizedIPs;
        return this;
    }

    /* Get Properties */
    public int getMemberID() {
        return memberID;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
    
    public boolean isRecognizedIP(String ip) {
        return recognizedIPs.contains(ip);
    }
    
    public boolean isLoggedIn() {
        return memberID != EMPTY.memberID;
    }

    public boolean addRecognizedIP(String ip) {
        return recognizedIPs.add(ip);
    }
}
/*
type: student/tutor/admin
memberid, session

optional:
    address
    first name
    last name
    
 
*/
