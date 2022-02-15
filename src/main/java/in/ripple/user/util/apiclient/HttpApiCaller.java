package in.ripple.user.util.apiclient;

import in.ripple.user.Exception.InternalException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * This implementation provides direct call to internal apis which are in the same service via method calls
 */
@Component("HttpApiCaller")
public class HttpApiCaller implements InternalApiClient {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(HttpApiCaller.class);

    @Autowired
    ApplicationContext applicationContext;

    @Value("${api.server.host}")
    String serverAddress;

    @Autowired
    HttpClientUtil httpClientUtil;

    @Override
    public String callApi(HttpServletRequest servletRequest, String apiName, String requestBody) throws InternalException {
        /**
         *  An api to service lookup  solution can be implemented here so that it goes to the specified service in case
         *  of multiple microservices
         */

        try {
            Map<String, String> finalHeader = new HashMap<>();
            final String uri = "http://" + serverAddress + "/" + apiName;
            finalHeader.put("is_authenticated","true");
            finalHeader.put("Authorization",servletRequest.getHeader("Authorization"));
            final HttpClientUtil.StringResponse resp = httpClientUtil.post4(uri, finalHeader, requestBody);

            if (null != resp && resp.getStatusCode() == 200) {
                return resp.getContent();
            } else {
                throw new Exception("Error while calling");
            }
        } catch (Exception e) {
            LOG.error("Exception: ", e);
            throw new InternalException(e.getMessage());
        }
    }
}
