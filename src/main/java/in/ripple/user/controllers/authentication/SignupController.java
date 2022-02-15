package in.ripple.user.controllers.authentication;

import com.google.gson.Gson;
import in.ripple.user.util.ErrorResponse;
import in.ripple.user.exception.DuplicateUserException;
import in.ripple.user.exception.InternalException;
import in.ripple.user.constants.UserConstants;
import in.ripple.user.controllers.AbstractRestController;
import in.ripple.user.controllers.authentication.model.SignupRequest;
import in.ripple.user.controllers.authentication.model.SignupResponse;
import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.entity.UserEntity;
import in.ripple.user.util.HashUtil;
import in.ripple.user.util.Validator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${master.role.id}")
    String masterId;

    @Value("${default.role.id}")
    String defaultId;


    @Override
    @CrossOrigin
    @PostMapping(value = "/{userRole}/signUp", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws Exception {
        LOG.info("Started signup");

        final SignupRequest signupRequest = new Gson().fromJson(jsonRequest, SignupRequest.class);


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

            Long roleId = Long.parseLong(defaultId);

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
                    if (role.equalsIgnoreCase("MASTER")) {
                        roleId = Long.parseLong(masterId);
                    } else {
                        roleId = Long.parseLong(defaultId);
                    }
                }


                final Date now = new Date();

                final String password = signupRequest.getPassword();

                final String base64DecryptedPassword = new String(HashUtil.decodeBase64(password));

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


            } else {
                LOG.error("Duplicate user info");
                throw new DuplicateUserException("Duplicate user info found");
            }
        } catch (InternalException e) {
            return new ResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (DuplicateUserException e) {
            return new ResponseEntity(new ErrorResponse(HttpStatus.CONFLICT, "Duplicate User Details"), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Some internal error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        LOG.info("User signed up successfully");
        SignupResponse signupResponse = new SignupResponse("200", "Signup Success");

        return new ResponseEntity(signupResponse, HttpStatus.OK);

    }

    Boolean isUnique(String type, String value) {
        boolean isUnique = true;

        if (UserConstants.EMAIL.equalsIgnoreCase(type)) {
            isUnique = userDaoService.findByEmail(value) == null;
        } else if (UserConstants.MOBILE.equalsIgnoreCase(type)) {
            isUnique = userDaoService.findByMobile(value) == null;
        } else if (UserConstants.USER_NAME.equalsIgnoreCase(type)) {
            isUnique = userDaoService.findByUserName(value) == null;
        }

        return isUnique;
    }

}
