package in.ripple.user.Exception;

public class DuplicateUserException extends CustomException {


    public DuplicateUserException(String errorMessage) {
        super(errorMessage,errorMessage);

    }

    public DuplicateUserException(String message, String errorMessage) {
        super(message, errorMessage);
    }
}
