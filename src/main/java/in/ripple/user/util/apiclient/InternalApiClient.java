package in.ripple.user.util.apiclient;

import in.ripple.user.Exception.InternalException;

import javax.servlet.http.HttpServletRequest;
/**
 * provides an interface to the internal api caller in microservice architecture
 * for this example since everything is tied in a single application so one implementation is provided.
 * Example 1. interface can be implemented by HttpClientUtil which can do http calls to external microservices
 * Example 2. This interface can be implemented by a class which calls the api from same microservice by doing method calls
 */
public interface InternalApiClient {

    public String callApi(HttpServletRequest servletRequest,String apiName, String requestBody) throws InternalException;

}
