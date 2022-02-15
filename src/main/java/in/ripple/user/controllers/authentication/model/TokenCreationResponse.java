package in.ripple.user.controllers.authentication.model;

public class TokenCreationResponse {
    private  String jwttoken;

    public String getJwttoken() {
        return jwttoken;
    }

    public void setJwttoken(String jwttoken) {
        this.jwttoken = jwttoken;
    }
}
