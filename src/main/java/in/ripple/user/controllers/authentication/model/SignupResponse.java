package in.ripple.user.controllers.authentication.model;

import in.ripple.user.util.AbstractResponse;

public class SignupResponse extends AbstractResponse {

    public SignupResponse() {
    }

    public SignupResponse(String status, String message) {
        super(status, message);
    }
}
