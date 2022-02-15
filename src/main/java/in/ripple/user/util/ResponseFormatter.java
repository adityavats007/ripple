package in.ripple.user.util;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ResponseFormatter {
    public JSONObject formatSuccessResponse(Map resMap){
        JSONObject res = new JSONObject();
        Set set = resMap.keySet();

        if(set.size()>0) {
            res.putAll(resMap);
        }
        JSONObject successResponse= new JSONObject();
        successResponse.put("status","SUCCESS");
        successResponse.put("code","SUCCESS");
        successResponse.put("message","SUCCESS");
        successResponse.put("response",resMap);
        return successResponse;
    }

    public JSONObject formatFailureResponse(Map resMap){
        JSONObject res = new JSONObject();
        Set set = resMap.keySet();

        if(set.size()>0) {
            res.putAll(resMap);
        }
        JSONObject failureResponse= new JSONObject();
        failureResponse.put("status","Failure");
        failureResponse.put("code",resMap.getOrDefault("httpCode",500));
        failureResponse.put("message",resMap.getOrDefault("message","Failure"));
        //failureResponse.put("response",resMap);
        return failureResponse;

    }
}
