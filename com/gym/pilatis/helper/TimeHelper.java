package com.gym.pilatis.helper;

public class TimeHelper {

    public static final TimeHelper INSTANCE = new TimeHelper();

    public static final int getSeconds(int day, int hour, int minutes, int seconds) {
        return ((day * 24 + hour) * 60 + minutes) * 60 + seconds;
    }

    public static final int getSecondsByMinute(int minutes) {
        return minutes * 60;
    }

    public static final int getSecondsByHour(int hour) {
        return hour * 60 * 60;
    }

    public static final int getSecondsByDay(int day) {
        return day * 24 * 60 * 60;
    }
}
