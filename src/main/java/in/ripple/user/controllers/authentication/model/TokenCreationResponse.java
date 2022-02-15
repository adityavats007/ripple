package in.ripple.user.controllers.authentication.model;

import in.ripple.user.util.AbstractResponse;

public class TokenCreationResponse extends AbstractResponse {
    private  String jwttoken;

    public String getJwttoken() {
        return jwttoken;
    }

    public void setJwttoken(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public TokenCreationResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public TokenCreationResponse() {
    }



    public TokenCreationResponse(String status, String message, String jwttoken) {
        super(status, message);
        this.jwttoken = jwttoken;
    }
}
