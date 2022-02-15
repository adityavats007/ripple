package in.ripple.user.controllers.authentication;

import com.google.gson.Gson;
import in.ripple.user.Exception.DuplicateUserException;
import in.ripple.user.Exception.InternalException;
import in.ripple.user.constants.UserConstants;
import in.ripple.user.controllers.AbstractRestController;
import in.ripple.user.controllers.authentication.model.SignupRequest;
import in.ripple.user.persistence.dao.RoleDaoService;
import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.entity.UserEntity;
import in.ripple.user.util.HashUtil;
import in.ripple.user.util.Validator;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@RestController
@Component("signUp")
public class SignupController extends AbstractRestController {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SignupController.class);


    @Autowired
    UserDaoService userDaoService;

    @Autowired
    Validator validator;

    @Autowired
    RoleDaoService roleDaoService;


    @Override
    @CrossOrigin
    @PostMapping(value = "signUp", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws Exception {
        LOG.info("Started signup");
        final SignupRequest signupRequest = new Gson().fromJson(jsonRequest, SignupRequest.class);

        final JSONObject response = new JSONObject();

        try {
            final String email = signupRequest.getEmail();

            final String mobile = signupRequest.getMobileNumber();

            if (!(validator.isValidMobileNumber(mobile) && validator.isValidEmail(email))) {
                LOG.error("Invalid input");
                throw new InternalException("Invalid input");
            }

            Boolean isMobileUnique = isUnique("MOBILE", mobile);

            Boolean isEmailUnique = isUnique("EMAIL", email);
            String username = signupRequest.getUserName();

            final Boolean userNameIsEmail = signupRequest.getUserNameSameAsEmail();

            if (userNameIsEmail) {
                username = email;
            }

            Boolean isUserNameUnique = isUnique("USER_NAME", username);

            Long roleId=roleDaoService.getRoleFromName("DEFAULT").getId();

            if (isMobileUnique && isEmailUnique && isUserNameUnique) {

                String role = signupRequest.getRole();

                if (StringUtils.isEmpty(role)) {
                    //if role is not coming in request just assign a default role
                    role = "DEFAULT";
                } else {
                    // if role is coming in request then validate the role first
                    if (!validator.isValidRole(role)) {
                        LOG.error("Invalid role");
                        throw new InternalException("Invalid Role");
                    }
                    roleId=roleDaoService.getRoleFromName(role).getId();

                }


                final Date now = new Date();

                final String password = signupRequest.getPassword();

                final String base64DecryptedPassword= new String(HashUtil.decodeBase64(password));

                final String encryptedPassword = new String(HashUtil.generateSha256Hash(base64DecryptedPassword));


                UserEntity userEntity = new UserEntity();

                userEntity.setMobile(mobile);

                userEntity.setUserName(username);

                userEntity.setEmail(email);

                userEntity.setPassword(encryptedPassword);

                userEntity.setCreatedOn(now);

                userEntity.setRole(roleId);

                userEntity.setDeleted(false);

                userDaoService.save(userEntity);

                response.put("message", "Sign up successful");

            } else {
                LOG.error("Duplicate user info");
                throw new InternalException("Duplicate user info found");
            }
        } catch (Exception e) {
            if (e.getClass().getSimpleName().equalsIgnoreCase("DuplicateUserException")) {
                response.put("message", "Email or Mobile number already in use");
                throw e;
            } else {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
                throw new InternalException(e.getMessage());
            }
        }
        LOG.info("User signed up successfully");
        return response.toJSONString();

    }

    Boolean isUnique(String type, String value) {
        boolean isUnique = true;

        if (UserConstants.EMAIL.equalsIgnoreCase(type)) {
            isUnique = userDaoService.findByEmail(value) == null;
        } else if (UserConstants.MOBILE.equalsIgnoreCase(type)) {
            isUnique = userDaoService.findByMobile(value) == null;
        }else if (UserConstants.USER_NAME.equalsIgnoreCase(type)) {
            isUnique = userDaoService.findByMobile(value) == null;
        }

        return isUnique;
    }

}
