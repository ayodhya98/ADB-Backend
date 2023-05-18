package it.adbconstructions.adb_api.service;

import it.adbconstructions.adb_api.exception.*;
import it.adbconstructions.adb_api.model.Consumer;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface IConsumerService {

    Consumer register(String firstName, String lastName, String username, String email, String password, String city, String state) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException;

    List<Consumer> getUsers();

    Consumer findUserByUserName(String username);

    Consumer findUserByEmail(String email);

    Consumer addNewUser(String firstName,
                    String lastName, String username, String email, String role,
                    boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    Consumer updateUser(String currentUsername, String newFirstName,
                    String newLastName, String newUsername, String newEmail, String role,
                    boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    Consumer updateUserProfileDetails(String currentUsername, String newFirstName,
                                  String newLastName, String newUsername, String newEmail, String role, String city, String state,
                                  boolean isNotLocked, boolean isActive) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

    Consumer updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    Consumer timeBasedPasswordReset(String username, String code, String password) throws ResetCodeExpiredException;
}