package com.gym.pilatis.account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gym.pilatis.HTTP;
import com.gym.pilatis.Handler;
import com.gym.pilatis.helper.DataHelper;
import com.gym.pilatis.helper.TimeHelper;
import com.gym.pilatis.login.LoginManager;
import com.oracle.tools.packager.Log;

public class AccountHandler extends Handler {

    public AccountHandler() {
        super("ac");
    }

    @Override
    public void init() {
        
    }

    @Override
    public boolean handle(HTTP http) {
        logger.debug("hihi ");
        String command = http.getCommand().originalCommand;
        if (command.equals("ac-reg")) {
            return register(http);
        }
        if (command.equals("ac-login")) {
            return login(http);
        }
        if (command.equals("ac-logout")) {
            return logout(http);
        }

        return false;
    }

    public boolean logout(HTTP http) {
        removeLogIN(http);
        return http.redirectCurrentPage();
    }

    public boolean login(HTTP http) {
        // 1. Verify member and create from DB
        String email = http.getParam("userEmail");
        String pass = http.getParam("userPass");

        LoginModel loginModel = new LoginModel(http);
        loginModel.setRequiredFieldsAndGatherErrorCode(email, pass);

        if (loginModel.isInvalid()) {
            System.out.print("Email or Password invalid : " + loginModel.getRequiredFieldsErrorCode());
            return false;
        }
        Member member = AccountManager.INSTANCE.createMemberFromLogin(loginModel);
        // TODO validate request device, 
        // if email and password match but IP not recognized -> email alert
        if (member == Member.EMPTY) {
            System.out.print("Email or Password do not match our record : " + loginModel.getRequiredFieldsErrorCode());
            return false;
        }

        // 2. add member into cache 
        addNewLogInMember(member, http);

        return http.redirectCurrentPage();
    }

    public boolean register(HTTP http) {

        String username = http.getParam("userName");
        String email = http.getParam("userEmail");
        String pass = http.getParam("userPass");
        String address = http.getParam("userAddress");
        String phone = http.getParam("userPhone");

        LoginModel loginModel = new LoginModel(http);

        // 1. Verify member and create new member
        loginModel.setRequiredFieldsAndGatherErrorCode(email, pass);
        if (loginModel.isInvalid()) {
            System.out.print("Email or Password invalid : " + loginModel.getRequiredFieldsErrorCode());
            return false;
        }

        loginModel.setOptionalFieldsAndGatherErrorCode(username, address, phone);
        if (loginModel.getOptionalFieldsErrorCode() > 0) {
            System.out.print("User infomation errors : " + loginModel.getOptionalFieldsErrorCode());
            return false;
        }

        Member member = AccountManager.INSTANCE.createMemberFromRegistration(loginModel);
        if (member.equals(Member.EMPTY)) {
            System.out.print("Registration failed. Email already registered. : " + loginModel.getRequiredFieldsErrorCode());
            return false;
        }

        // 2. add member into cache 
        addNewLogInMember(member, http);

        return http.redirectCurrentPage();
    }

    private void addNewLogInMember(Member member, HTTP http) {
        String persistentKey = DataHelper.encode(http.getIP(), LoginManager.LOGIN_KEY_LENGTH);
        String sessionKey = DataHelper.encode(persistentKey, LoginManager.LOGIN_KEY_LENGTH);
        
        LoginManager.INSTANCE.addLoginSessionKey(member.getMemberID(), sessionKey);
        LoginManager.INSTANCE.addNewLoginRecord(member, http, persistentKey);
        AccountManager.INSTANCE.addNewLoginMember(member);
        http.setCookie(LoginManager.SESSION_KEY, sessionKey, TimeHelper.getSecondsByDay(7));
        http.setCookie(LoginManager.PERSISTENT_KEY, persistentKey, TimeHelper.getSecondsByDay(365));
        member.addRecognizedIP(http.getIP());
    }

    private void removeLogIN(HTTP http) {
        LoginManager.INSTANCE.removeLoginSessionKey(http.getShortTermKey());
        AccountManager.INSTANCE.removeLoginMember(http.getMember());
        http.deleteCookie(LoginManager.PERSISTENT_KEY);
        http.deleteCookie(LoginManager.SESSION_KEY);
    }

}
