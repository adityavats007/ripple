package in.ripple.user.controllers.gateway;

import in.ripple.user.Exception.UserNotFoundException;
import in.ripple.user.configuration.JwtTokenUtil;
import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.entity.UserEntity;
import in.ripple.user.controllers.authorization.AuthorizationUtil;
import in.ripple.user.util.apiclient.InternalApiClient;
import in.ripple.user.util.ResponseFormatter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Controller
public class GatewayController {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(GatewayController.class);


    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ResponseFormatter responseFormatter;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserDaoService userDaoService;

    @Autowired
    AuthorizationUtil authorizationUtil;

    @Value("${api.client.impl}")
    String apiImpl;



    //with authentication for registered clients
    @CrossOrigin
    @PostMapping(value = "/api/auth/{apiVersion}/{apiName}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String callInternalApi(HttpServletRequest httpRequest, @RequestBody String jsonRequest,
                             @PathVariable String apiVersion, @PathVariable String apiName, @RequestHeader Map<String, String> header) throws Exception {

        LOG.info("Started Validating JWT");
        final String token = httpRequest.getHeader("Authorization");
        final String userNameFromToken = jwtTokenUtil.getUserNameFromToken(token);// to check the api access is given or not
        UserEntity userEntity = null;
        if (!(apiName.equalsIgnoreCase("login") || apiName.equalsIgnoreCase("signUp"))) {
            try {
                userEntity = userDaoService.findByUserName(userNameFromToken);
                if (null == userEntity)
                    throw new UserNotFoundException("404", "User Not Found");
                //check for role access mapping if api is not login or signup
                boolean roleHasAccessToTheApi = checkRoleAccessMapping(userEntity, apiName);
                if (!roleHasAccessToTheApi) {
                    // permission denied
                    HashMap<String,String> map=new HashMap<>();
                    map.put("httpCode",HttpStatus.UNAUTHORIZED.toString());
                    map.put("message","Permission denied");

                    return responseFormatter.formatFailureResponse(map).toJSONString();
                }
            }
            catch (UserNotFoundException e){
                HashMap<String,String> map=new HashMap<>();
                map.put("httpCode",HttpStatus.NOT_FOUND.toString());
                map.put("message",e.getErrorMessage().toString());
                return responseFormatter.formatFailureResponse(map).toJSONString();
            }
            catch (Exception e){
                HashMap<String,String> map=new HashMap<>();
                map.put("httpCode",HttpStatus.INTERNAL_SERVER_ERROR.toString());
                map.put("message",e.getLocalizedMessage().toString());
                return responseFormatter.formatFailureResponse(map).toJSONString();
            }
        }

        String response = "";

        try {
            InternalApiClient apiClientUtil = (InternalApiClient) applicationContext.getBean(apiImpl.toString());
            response = apiClientUtil.callApi(httpRequest, apiName, jsonRequest);

        } catch (Exception e) {
            LOG.error("Error : ",e);
            HashMap<String,String> map=new HashMap<>();
            map.put("httpCode",HttpStatus.INTERNAL_SERVER_ERROR.toString());
            map.put("message",e.getMessage().toString());
            return responseFormatter.formatFailureResponse(map).toJSONString();
        }
        return response;
    }

    /**
     * @param userEntity
     * @param apiName
     * @return
     */
    private boolean checkRoleAccessMapping(UserEntity userEntity, String apiName) {
        List<String> roleAccessMappingList = new ArrayList<>();
        //get list of accessible apis for this role
        roleAccessMappingList = authorizationUtil.getApiAccessForRole(userEntity.getRole());
        //check if the role has the @apiName access if
        return roleAccessMappingList.contains(apiName.toUpperCase());
    }
}
