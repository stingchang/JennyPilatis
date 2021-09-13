package com.gym.pilatis.account;

public class MemberInfo {
    public static final MemberInfo EMPTY = new MemberInfo();
    
    // TODO Use REGEX to validate and create Object ?
    private String address;
    private String name;
    private String phone;
    
    protected MemberInfo() {}
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    } 
}
