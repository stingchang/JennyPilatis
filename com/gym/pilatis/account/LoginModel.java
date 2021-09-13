package com.gym.pilatis.account;

import java.util.Optional;

import org.joda.time.DateTime;

import com.gym.pilatis.HTTP;
import com.gym.pilatis.helper.GymHelper;

public class LoginModel {

    private int requiredFieldsErrorCode = 0;
    private int optionalFieldsErrorCode = 0;

    private String password;
    private String email;

    private MemberInfo memberInfo;
    private DateTime regTime;

    public LoginModel(HTTP http) {
        regTime = http.getRequestTime();
    }

    /* Set up */
    // TODO save reference to DB
    enum ErrorCodes {
        IS_VALID            (0     , ""),
        INVALID_PASSWORD    (1 << 0, "Password Invalid"), 
        INVALID_EMAIL       (1 << 1, "Email in valid"),
        INVALID_NAME        (1 << 2, "Name Invalid"),
        INVALID_ADDRESS     (1 << 3, "Address Invalid"),
        INVALID_PHONE       (1 << 4, "Address Phone Number"),
        ;

        private int code;
        private String msg;

        ErrorCodes(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        int getCode() {
            return code;
        }

        String getMessage() {
            return msg;
        }

        String getName() {
            return this.name();
        }
    }

    public void setRequiredFieldsAndGatherErrorCode(String email, String password) {

        requiredFieldsErrorCode |= validatePasswordAndGetErrorCode(password);
        requiredFieldsErrorCode |= validateEmailAndGetErrorCode(email);

        if (requiredFieldsErrorCode != 0) {
            return;
        }
        // TODO encrypt
        this.password = encrypt(password);
        this.email = email; 
    }

    public void setOptionalFieldsAndGatherErrorCode(String username, String address, String phone) {

        this.memberInfo = new MemberInfo();

        optionalFieldsErrorCode |= validateUsernameAndGetErrorCode(username);
        optionalFieldsErrorCode |= validateAddressAndGetErrorCode(address);
        optionalFieldsErrorCode |= validatePhoneNumberAndGetErrorCode(phone);
        
        this.memberInfo.setName(hasValidName() ? username : "");
        this.memberInfo.setAddress(hasValidAddress() ? address : "");
        this.memberInfo.setPhone(hasValidPhoneNumber() ? phone : "");
    }

    // TODO use REGEX to validate password & email
    private static final int validatePasswordAndGetErrorCode(String password) {
        if (GymHelper.isEmpty(password)) {
            return ErrorCodes.INVALID_PASSWORD.getCode();
        }
        return ErrorCodes.IS_VALID.getCode();
    }

    private static final int validateEmailAndGetErrorCode(String email) {
        if (GymHelper.isEmpty(email)) {
            return ErrorCodes.INVALID_EMAIL.getCode();
        }
        return ErrorCodes.IS_VALID.getCode();
    }

    private static final int validateUsernameAndGetErrorCode(String username) {
        if (GymHelper.isEmpty(username)) {
            return ErrorCodes.INVALID_NAME.getCode();
        }
        return ErrorCodes.IS_VALID.getCode();
    }

    private static final int validateAddressAndGetErrorCode(String address) {
        if (GymHelper.isEmpty(address)) {
            return ErrorCodes.INVALID_ADDRESS.getCode();
        }
        return ErrorCodes.IS_VALID.getCode();
    }
    
    private static final int validatePhoneNumberAndGetErrorCode(String phone) {
        if (GymHelper.isEmpty(phone)) {
            return ErrorCodes.INVALID_PHONE.getCode();
        }
        return ErrorCodes.IS_VALID.getCode();
    }

    // TODO add encruption
    private static final String encrypt(String string) {
        if (GymHelper.isEmpty(string)) {
            return "";
        }

        return string;
    }

    /* API */
    public boolean isInvalid() {
        return requiredFieldsErrorCode > 0;
    }
    public boolean hasValidAddress() {
        return ( optionalFieldsErrorCode & ErrorCodes.INVALID_ADDRESS.getCode() ) == 0;
    }
    public boolean hasValidName() {
        return ( optionalFieldsErrorCode & ErrorCodes.INVALID_NAME.getCode() ) == 0;
    }
    public boolean hasValidPhoneNumber() {
        return ( optionalFieldsErrorCode & ErrorCodes.INVALID_PHONE.getCode() ) == 0;
    }
    public int getRequiredFieldsErrorCode() {
        return requiredFieldsErrorCode;
    }

    public int getOptionalFieldsErrorCode() {
        return optionalFieldsErrorCode;
    }

    // TODO should only expose encryped password. 
    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return Optional.of(this.memberInfo.getName()).orElse("");
    }
    public String getAddress() {
        return Optional.of(this.memberInfo.getName()).orElse("");
    }
    
    public DateTime getRegTime() {
        return this.regTime;
    }
}
