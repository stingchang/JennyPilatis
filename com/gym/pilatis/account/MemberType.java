package com.gym.pilatis.account;

public enum MemberType {
    STUDENT(1), TUTOR(2), MANAGER(3);
    final int typeID;
    
    private MemberType(int typeID) {
        this.typeID = typeID;
    }
    
    public int getTypeID() {
        return typeID;
    }
    
}
