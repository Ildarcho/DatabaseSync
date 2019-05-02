package net.Ildar.DatabaseSync;

import java.sql.SQLException;

/**
 * Custom implementation of Exception class to hold any SQL error messages
 */
public class SQLCustomException extends Exception {

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message == null)
            return "";
        return message;
    }

    public SQLCustomException(String message) {
        super(message);
    }

    public SQLCustomException(String message, Exception cause) {
        super(message, cause);
    }

    public SQLCustomException(SQLException e) {
        super("SQL exception! Message - " + e.getMessage(), e);
    }
}
