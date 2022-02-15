package in.ripple.user.controllers.authentication;

import com.google.gson.Gson;
import in.ripple.user.util.ErrorResponse;
import in.ripple.user.exception.InternalException;
import in.ripple.user.exception.UserNotFoundException;
import in.ripple.user.configuration.JwtTokenUtil;
import in.ripple.user.configuration.JwtUserDetailsService;
import in.ripple.user.controllers.AbstractRestController;
import in.ripple.user.controllers.authentication.model.TokenCreationRequest;
import in.ripple.user.controllers.authentication.model.TokenCreationResponse;
import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.entity.UserEntity;
import in.ripple.user.util.HashUtil;
import in.ripple.user.util.Validator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Component("login")
public class LoginController extends AbstractRestController {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserDaoService userDaoService;

    @Autowired
    Validator validator;

    @Override
    @CrossOrigin
    @PostMapping(value = "/{userRole}/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws UserNotFoundException, InternalException {
        TokenCreationResponse response = new TokenCreationResponse();
        TokenCreationRequest tokenCreationRequest = new Gson().fromJson(jsonRequest, TokenCreationRequest.class);
        try {

            try {
                if (!(validator.isValidString(tokenCreationRequest.getUsername()) && validator.isValidString(tokenCreationRequest.getPassword()))) {
                    LOG.error("Invalid input");
                    throw new InternalException("Invalid input");
                }
                authenticateUser(tokenCreationRequest.getUsername(), tokenCreationRequest.getPassword());

            } catch (UserNotFoundException e) {
                throw e;
            } catch (Exception e) {
                throw new InternalException(e.getMessage(), e.getMessage());
            }
            final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(tokenCreationRequest.getUsername());

            String token = generateToken(userDetails);

            response.setJwttoken(token);
        } catch (UserNotFoundException e) {
            return new ResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()), HttpStatus.NOT_FOUND);

        } catch (InternalException e) {
            return new ResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        response.setMessage("Login success");
        response.setCode("200");
        return new ResponseEntity(response, HttpStatus.OK);
    }

    private String generateToken(UserDetails userDetails) {
        return jwtTokenUtil.generateToken(userDetails);
    }

    private void authenticateUser(String username, String password) throws UserNotFoundException, InternalException {

        UserEntity userEntity = userDaoService.findByUserName(username);

        if (null == userEntity) {

            LOG.info("No user found");

            throw new UserNotFoundException("No user found", "No user found");
        } else {

            String dbPassword = userEntity.getPassword();

            String decryptedBase64Password = new String(HashUtil.decodeBase64(password));

            String sha256Value = new String(HashUtil.generateSha256Hash(decryptedBase64Password));

            if (!sha256Value.equals(dbPassword)) {
                throw new InternalException("password invalid");
            }

        }

    }

}
