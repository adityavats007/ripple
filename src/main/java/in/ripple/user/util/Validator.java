package in.ripple.user.util;

import in.ripple.user.constants.UserConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class Validator {

    public boolean isValidString(String string) {
        return !StringUtils.isEmpty(string);
    }

    public boolean isValidEmail(String email) {
        return email.contains("@");
    }

    public boolean isValidMobileNumber(String mobileNumber) {
        try {
            Long.parseLong(mobileNumber);
        } catch (NumberFormatException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    public boolean isValidDiskSize(long diskSize) {
        return diskSize > 0;
    }

    public boolean isValidRole(String role) {
        return (role.equals(UserConstants.MASTER_ROLE) || role.equals(UserConstants.NON_MASTER_ROLE));
    }

}
