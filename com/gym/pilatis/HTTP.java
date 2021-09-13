package com.gym.pilatis;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;

import com.gym.pilatis.account.Member;
import com.gym.pilatis.helper.GymHelper;
import com.gym.pilatis.login.LoginManager;
import com.gym.pilatis.page.JspHandler;

public class HTTP {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext context;

    private final String[] urlPath;
    private final Command command;
    private Member member;
    private final HashMap<String, Cookie> cookies;
    private final DateTime requestTime;
    private final RequestDevice device;

    public class Command {
        public final String originalCommand;
        public final String prefix;
        public final String body;

        private Command(String command) {
            originalCommand = command;
            String[] commandArr = originalCommand.split("-");
            if (commandArr.length < 2 || GymHelper.isEmpty(commandArr[0]) || GymHelper.isEmpty(commandArr[1])) {
                prefix = body = "";
                return;
            }
            prefix = commandArr[0];
            body = commandArr[1];
        }

        public boolean hasCommand() {
            return GymHelper.isNonEmpty(prefix) && GymHelper.isNonEmpty(body);
        }
    }

    public HTTP(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
        this.response = response;
        this.request = request;
        this.context = context;
        this.command = new Command(getParam("cmd", ""));
        this.urlPath = request.getRequestURI().split("/");
        this.cookies = setCookies(new HashMap<String, Cookie>());
        this.requestTime = DateTime.now();
        this.device = new RequestDevice(request.getHeader("User-Agent"));
        LoginManager.INSTANCE.setHttpMember(this);
    }

    /* Provide functionalities */
    public boolean redirect(String path) {
        try {
            if(!path.startsWith("/")) {
                path = "/"+path;
            }
            
            /*********************************************************************/
            /* This is the only place to redirect jsp page, append http here     */
            /*********************************************************************/
//            request.setAttribute("http", this);
            response.sendRedirect(path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean redirectHome() {
        return redirect(JspHandler.getRootName());
    }
    
    public boolean redirectCurrentPage() {
        if(urlPath.length > 2) {
            return redirect(urlPath[2]);
        }
        return redirectHome();
    }

    public boolean forward(String path) {
        try {
            request.setAttribute("http", this);
            context.getRequestDispatcher(path).forward(request, response);
            return true;
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
//
//    public boolean forwardToJSP(String jspPath) {
//        return true;
//    }
//
//    public boolean forwardToJSP(String jspPath, String defaultPath) {
//        return true;
//    }

    public void write(String input) {
        try {
            response.getWriter().append(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Set Properties */
    public void setMember(Member member) {
        this.member = member;
    }

    private HashMap<String, Cookie> setCookies(HashMap<String, Cookie> cookies) {
        if (GymHelper.isNonEmpty(this.request.getCookies())) {
            for (Cookie c : this.request.getCookies()) {
                cookies.put(c.getName(), c);
            }
        }
        return cookies;
    }

    public void setAttribute(String name, Object value) {
        request.setAttribute(name, value);
    }

    public void setCookie(String key, String value, int expiry) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expiry);
        cookies.put(key, cookie);
        response.addCookie(cookie);
    }
    
    public void deleteCookie(String key) {
        Cookie c = cookies.remove(key);
        if(c == null) {
            c = new Cookie(key, "");
        }
        c.setMaxAge(0);
        response.addCookie(c);
    }
    
    /* Get Properties */

    public HttpSession getSession() {
        return request.getSession(true);
    }

    public String getSessionID() {
        return request.getRequestedSessionId();
    }

    public StringBuffer getRequestURL() {
        return request.getRequestURL();
    }

    public String getRequestURI() {
        return request.getRequestURI();
    }

    public String getContextPath() {
        return request.getContextPath();
    }

    public String getScheme() {
        return request.getScheme();
    }

    public String getServerName() {
        return request.getServerName();
    }

    public int getServerPort() {
        return request.getServerPort();
    }

    public String getQueryString() {
        return request.getQueryString();
    }

    public String[] getUrlPathArray() {
        return urlPath;
    }

    public boolean isPost() {
        return request.getMethod().equals("POST");
    }

    public String getPageName() {
        return urlPath.length > 2 ? urlPath[2] : "";
    }

    public Command getCommand() {
        return command;
    }

    public String getParam(String paramName) {
        return request.getParameter(paramName);
    }

    public DateTime getRequestTime() {
        return requestTime;
    }
    
    public String getParam(String paramName, String defaultValue) {
        String paramValue = getParam(paramName);
        return GymHelper.isEmpty(paramValue) ? defaultValue : paramValue;
    }

    public int getIntParam(String paramName) {
        return Integer.parseInt(getParam(paramName));
    }

    public int getIntParam(String paramName, int defaultValue) {
        return Integer.parseInt(getParam(paramName, String.valueOf(defaultValue)));
    }

    public double getDoubleParam(String paramName) {
        return Double.parseDouble(getParam(paramName));
    }

    public double getDoubleParam(String paramName, double defaultValue) {
        return Double.parseDouble(getParam(paramName, String.valueOf(defaultValue)));
    }

    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    public Member getMember() {
        return this.member;
    }

    public String getLongTermKey() {
        String keyName = LoginManager.PERSISTENT_KEY;
        return cookies.get(keyName) == null ? null : cookies.get(keyName).getValue();
    }

    public String getShortTermKey() {
        String keyName = LoginManager.SESSION_KEY;
        return cookies.get(keyName) == null ? null : cookies.get(keyName).getValue();
    }
    
    public String getIP() {
        return request.getRemoteAddr();
    }
    
    public RequestDevice getDevice() {
        return device;
    }
}

/*
//URL usage
//https://stackoverflow.com/questions/16675191/get-full-url-and-query-string-in-servlet-for-both-http-and-https-requests/16675399
// 
//http://localhost:8080/pilatis/path1/path2?id=5&cmd=ac-test-something
//request.getScheme():      http
//request.getServerName(): localhost
//request.getServerPort(): 8080
//request.getRequestURI(): /pilatis/path1/path2
//request.getQueryString():id=5&cmd=ac-test-something
//URL path:                 [, pilatis, path1, path2]
//getContextPath    :         /pilatis
//
// Code example
// String uri = request.getScheme() + "://" +
//             request.getServerName() + 
//             ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) +
//             request.getRequestURI() +
//            (request.getQueryString() != null ? "?" + request.getQueryString() : "");
// 

persistent key and serssion key, see LoginManager

 * */