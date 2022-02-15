package in.ripple.user.exception;

public class DuplicateUserException extends CustomException {


    public DuplicateUserException(String errorMessage) {
        super(errorMessage, errorMessage);

    }

    public DuplicateUserException(String message, String errorMessage) {
        super(message, errorMessage);
    }
}
