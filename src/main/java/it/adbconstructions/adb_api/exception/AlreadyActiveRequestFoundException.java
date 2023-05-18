package it.adbconstructions.adb_api.exception;

public class AlreadyActiveRequestFoundException extends Exception {
    public AlreadyActiveRequestFoundException(String message) {
        super(message);
    }
}
