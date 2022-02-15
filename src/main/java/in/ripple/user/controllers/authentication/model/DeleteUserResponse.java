package in.ripple.user.controllers.authentication.model;

import in.ripple.user.controllers.AbstractResponse;

public class DeleteUserResponse extends AbstractResponse {

    public DeleteUserResponse() {
    }

    public DeleteUserResponse(String status, String message) {
        super(status, message);
    }
}
