package in.ripple.user.exception;

public abstract class CustomException extends Exception {


    private String errorMessage;

    public CustomException(String errorMessage) {

        this.errorMessage = errorMessage;
    }

    public CustomException(String message, String errorMessage) {
        super(message);

        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
