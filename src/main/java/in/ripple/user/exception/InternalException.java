package in.ripple.user.exception;

public class InternalException extends CustomException{

    public InternalException(String errorMessage) {
        super(errorMessage, errorMessage);
    }

    public InternalException(String message, String errorMessage) {
        super(message, errorMessage);
    }
}
