package com.gym.pilatis;

class RequestDevice {
    /* https://stackoverflow.com/questions/1326928/how-can-i-get-client-information-such-as-os-and-browser/18030465 */

    String os;
    String browser;
    public static final String UNKNOWN_OS = "Unknown OS";
    public static final String UNKNOWN_BROWSER = "Unknown Browser";

    // store HTTP user agent info 
    RequestDevice(String userAgent) {
        os = getOS(userAgent);
        browser = getBrowser(userAgent);
    }

    private static String getOS(String userAgent) {
        //=================OS=======================
        if (userAgent.toLowerCase().indexOf("windows") >= 0) {
            return "Windows";
        } else if (userAgent.toLowerCase().indexOf("mac") >= 0) {
            return "Mac";
        } else if (userAgent.toLowerCase().indexOf("x11") >= 0) {
            return "Unix";
        } else if (userAgent.toLowerCase().indexOf("android") >= 0) {
            return "Android";
        } else if (userAgent.toLowerCase().indexOf("iphone") >= 0) {
            return "IPhone";
        }
        return UNKNOWN_OS;

    }

    private static String getBrowser(String userAgent) {
        String user = userAgent.toLowerCase();

        if (user.contains("msie")) {
            String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
            return substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version")) {
            return (userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0] + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if (user.contains("opr") || user.contains("opera")) {
            if (user.contains("opera"))
                return (userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0] + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            else if (user.contains("opr"))
                return ((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");
        } else if (user.contains("chrome")) {
            return (userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1) || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {
            //
            return "Netscape-?";
        } else if (user.contains("firefox")) {
            return (userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("rv")) {
            return "IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
        }
        return UNKNOWN_BROWSER;

    }

    public boolean isValidDevice() {
        return !browser.equals(UNKNOWN_OS) || !browser.equals(UNKNOWN_BROWSER);
    }
    
    public boolean isMobile() {
        return os.equals("Android") || os.equals("IPhone"); 
    }
    
    // TODO
    public boolean isTablet() {
        return false;
    }

    @Override
    public String toString() {
        return "OS: " + os + " ; BROWSER: " + browser + ";";
    }
}