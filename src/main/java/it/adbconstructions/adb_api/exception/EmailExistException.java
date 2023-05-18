package it.adbconstructions.adb_api.exception;

public class EmailExistException extends Exception{

    public EmailExistException(String message) {
        super(message);
    }
}
