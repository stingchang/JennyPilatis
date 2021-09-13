package com.gym.pilatis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Handler {
    private String pattern;
//    Logger logger = LogManager.getRootLogger();
    protected static final Logger logger = LogManager.getLogger(Handler.class.getName());
    
    @SuppressWarnings("unused")
    private Handler() {
    }

    protected Handler(String pattern) {
        this.pattern = pattern;
    }

    public abstract void init();

    public final boolean acceptAndHandle(HTTP http) {
        return accept(http) && handle(http);
    }

    protected boolean accept(HTTP http) {
        return http.getCommand().prefix.equals(this.pattern);
    }

    public abstract boolean handle(HTTP http);

    public final String getPattern() {
        return pattern;
    }
}
