package in.ripple.user.controllers.authentication.model;

import lombok.NonNull;

public class SignupRequest {

    @NonNull
    String userName;

    @NonNull
    String email;

    @NonNull
    String mobileNumber;

    @NonNull
    String password;

    @NonNull
    String role;

    @NonNull
    Boolean isUserNameSameAsEmail;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getUserNameSameAsEmail() {
        return isUserNameSameAsEmail;
    }

    public void setUserNameSameAsEmail(Boolean userNameSameAsEmail) {
        isUserNameSameAsEmail = userNameSameAsEmail;
    }
}
