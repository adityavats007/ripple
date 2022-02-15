package in.ripple.user.util.apiclient;

import in.ripple.user.Exception.InternalException;
import in.ripple.user.controllers.AbstractRestController;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;

/**
 * This implementation provides direct call to internal apis which are in the same service via method calls
 */
@Component("SimilarServiceApiCaller")
public class SimilarServiceApiCaller implements InternalApiClient {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SimilarServiceApiCaller.class);

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public String callApi(HttpServletRequest servletRequest,String apiName, String requestBody) throws InternalException {
        final AbstractRestController controller = (AbstractRestController) applicationContext.getBean(apiName);
        String response="";
        try {
            response=controller.process(servletRequest,requestBody);
        } catch (InternalException exception) {
            //log the exception
            LOG.error("Error while calling api {}",apiName,exception);
            throw  exception;
        } catch (Exception e){
            LOG.error("Error while processing internal api call ",e);
            throw new InternalException(e.getMessage());
        }
        return response;
    }
}
