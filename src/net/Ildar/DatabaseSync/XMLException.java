package net.Ildar.DatabaseSync;

/**
 * Custom implementation of Exception class to hold any XML error messages
 */
public class XMLException extends Exception {
    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message == null)
            return "";
        return message;
    }

    public XMLException(String message) {
        super(message);
    }

    public XMLException(String message, Exception cause) {
        super(message, cause);
    }
}
