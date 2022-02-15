package in.ripple.user.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public abstract class AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRestController.class);

    public abstract Object process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws Exception;

}
