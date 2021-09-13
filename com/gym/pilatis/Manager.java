package com.gym.pilatis;

import com.gym.pilatis.helper.DBHelper;
import com.gym.pilatis.helper.TimeHelper;

public abstract class Manager {
    /*
     1. db 
     2. object mapper
     3. logger
     */

    public DBHelper db = DBHelper.INSTANCE;
    public TimeHelper time = TimeHelper.INSTANCE;
    // public DataHelper data

}
