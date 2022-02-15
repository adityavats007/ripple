package in.ripple.user.controllers.authentication;

import com.google.gson.Gson;
import com.sun.media.jfxmedia.logging.Logger;
import in.ripple.user.Exception.InternalException;
import in.ripple.user.Exception.UserNotFoundException;
import in.ripple.user.configuration.JwtTokenUtil;
import in.ripple.user.configuration.JwtUserDetailsService;
import in.ripple.user.controllers.AbstractRestController;
import in.ripple.user.controllers.authentication.model.TokenCreationRequest;
import in.ripple.user.controllers.authentication.model.TokenCreationResponse;
import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.entity.UserEntity;
import in.ripple.user.util.HashUtil;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @Override
    @CrossOrigin
    @PostMapping(value = "login", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws UserNotFoundException,InternalException {

        TokenCreationRequest tokenCreationRequest = new Gson().fromJson(jsonRequest, TokenCreationRequest.class);

        try {

            authenticateUser(tokenCreationRequest.getUsername(), tokenCreationRequest.getPassword());

        }
        catch (UserNotFoundException e){
            throw e;
        }
        catch (Exception e){
            throw new InternalException(e.getMessage(),e.getMessage());
        }
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(tokenCreationRequest.getUsername());

        String token = generateToken(userDetails);

        JSONObject responseObject= new JSONObject();

        responseObject.put("token",token);

        responseObject.put("message","Login successful");

        ResponseEntity.ok();

        return responseObject.toJSONString();
    }

    private String generateToken(UserDetails userDetails){
        return jwtTokenUtil.generateToken(userDetails);
    }

    private void authenticateUser(String username, String password) throws Exception {
        try {
                UserEntity userEntity= userDaoService.findByUserName(username);

                if(null==userEntity){

                    LOG.info("No user found");

                    ResponseEntity.status(HttpStatus.NOT_FOUND);

                    throw new InternalException("No user found","No user found");
                }else {

                    String dbPassword = userEntity.getPassword();

                    String decryptedBase64Password=new String(HashUtil.decodeBase64(password));

                    String sha256Value   = new String(HashUtil.generateSha256Hash(decryptedBase64Password));

                    if(!sha256Value.equals(dbPassword)){

                       throw new InternalException("password invalid");
                   }

                }
        } catch (InternalException e) {

            throw new InternalException( e.getErrorMessage());

        }
    }

}
