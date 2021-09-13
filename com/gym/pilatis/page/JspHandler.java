package com.gym.pilatis.page;

import java.util.HashMap;

import com.gym.pilatis.HTTP;
import com.gym.pilatis.Handler;

public class JspHandler extends Handler {

    private static final HashMap<String, String> pageNameToJspFile = new HashMap<>();

    private final static class Path {
        final static String ROOT_NAME = "pilatis";
        final static String JSP_ROOT = "/page/";
        final static String JSP_EXTENSION = ".jsp";

        // TODO JSP Path should be dynamically declared. 
        final static String JSP_HOME        = JSP_ROOT + "home" + JSP_EXTENSION;
        final static String JSP_CALENDAR    = JSP_ROOT + "calendar" + JSP_EXTENSION;
        final static String JSP_TUTOR       = JSP_ROOT + "tutor" + JSP_EXTENSION;
        final static String JSP_REG_FORM    = JSP_ROOT + "regForm" + JSP_EXTENSION;
    }

    public JspHandler() {
        super("-");
        init();
    }

    @Override
    public void init() {
        pageNameToJspFile.put("home", Path.JSP_HOME);
        pageNameToJspFile.put("tutor", Path.JSP_TUTOR);
        pageNameToJspFile.put("calendar", Path.JSP_CALENDAR);
        pageNameToJspFile.put("regForm", Path.JSP_REG_FORM);

    }

    @Override
    public boolean accept(HTTP http) {
        return pageNameToJspFile.containsKey(http.getPageName());
    } 
    
    public boolean handle(HTTP http) {
        String pageName = http.getPageName();
        
        if (pageNameToJspFile.containsKey(pageName)) {
            String path = (pageNameToJspFile.get(pageName));
            
            return http.forward(path);
        }

        return false;
    }

    public boolean forwardToHome(HTTP http) {
        return http.forward(pageNameToJspFile.get("home"));
    }

    public static String getRootName() {
        return Path.ROOT_NAME;
    }

}
