package it.adbconstructions.adb_api.exception;

public class ProjectAlreadyExistException extends Exception {
    public ProjectAlreadyExistException(String message) {
        super(message);
    }
}
