package it.adbconstructions.adb_api.service;

import it.adbconstructions.adb_api.exception.*;
import it.adbconstructions.adb_api.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface IUserService {

    User register(String firstName, String lastName, String username, String email, String city, String state) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException;

    List<User> getUsers();

    User findUserByUserName(String username);

    User findUserByEmail(String email);

    User addNewUser(String firstName,
                    String lastName, String username, String email, String role,String city,String state,
                    boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException, MessagingException;

    User updateUser(String currentUsername,String newFirstName,
                    String newLastName, String newUsername, String newEmail, String role,String city,String state,
                    boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    User updateUserProfileDetails(String currentUsername,String newFirstName,
                                  String newLastName, String newUsername, String newEmail, String role,String city,String state,
                                  boolean isNotLocked, boolean isActive) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;
    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    User timeBasedPasswordReset(String username,String code, String password) throws ResetCodeExpiredException;

    List<String> getConsultantUsernames();
}
