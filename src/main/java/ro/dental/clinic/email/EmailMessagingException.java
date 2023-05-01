package ro.dental.clinic.email;

/**
 * Custom exception thrown when an email sending error is encountered
 */
public class EmailMessagingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmailMessagingException(String errorMsg) {
        super(errorMsg);
    }

    public EmailMessagingException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }
}
