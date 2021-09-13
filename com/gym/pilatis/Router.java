package com.gym.pilatis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.gym.pilatis.account.AccountHandler;
import com.gym.pilatis.course.ScheduleHandler;
import com.gym.pilatis.helper.GymHelper;
import com.gym.pilatis.page.JspHandler;

public class Router {

    private static final Map<String, Handler> commandHandlerCache = new HashMap<>();
    private final JspHandler jspHandler = new JspHandler();

    public void init() {
        installHandlers();
    }

    private void installHandlers() {
        Handler[] handlers = new Handler[] { 
                new AccountHandler(),
                new ScheduleHandler()
        };
        
        Arrays.stream(handlers).forEach((handler) -> {
            handler.init();
            commandHandlerCache.put(handler.getPattern(), handler);
        });
        jspHandler.init();
    }


    public boolean route(HTTP http) {
        boolean handled = false;
        try {
            // 1  len < 2 || root is invalid: log and redirect home
            // 2. len = 2, no command : forward home
            // 3. len = 2, with command:  try handlers
            // 4. len >= 3, try jspHandler 

            int pathLen = http.getUrlPathArray().length;
            
            if (pathLen < 2 || !http.getUrlPathArray()[1].equals(JspHandler.getRootName())) {
                http.redirect(JspHandler.getRootName());
                handled = true;
            }

            else if (pathLen == 2) {
                if (!http.isPost() && GymHelper.isEmpty(http.getQueryString())) {
                    handled |= jspHandler.forwardToHome(http);
                } else {
                    String commandPrefix = http.getCommand().prefix;
                    if (commandHandlerCache.containsKey(commandPrefix)) {
                        handled |= commandHandlerCache.get(commandPrefix).acceptAndHandle(http);
                    }
                }
            }

            // Request includes JSP page path, try to route to JSP 
            else {
                handled |= jspHandler.acceptAndHandle(http);
            }

        } catch (Exception e) {
            System.out.println("Request failed exception, info : " + e.toString());
            e.printStackTrace();
        }
        // TODO No handler or JSP page found, 404
        if (!handled) {
            http.redirect(http.getContextPath());
        }

        return handled;
    }
}
