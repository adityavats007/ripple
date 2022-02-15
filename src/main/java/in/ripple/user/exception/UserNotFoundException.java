package in.ripple.user.exception;

public class UserNotFoundException extends CustomException {


    public UserNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public UserNotFoundException(String message, String errorMessage) {
        super(message, errorMessage);
    }
}
