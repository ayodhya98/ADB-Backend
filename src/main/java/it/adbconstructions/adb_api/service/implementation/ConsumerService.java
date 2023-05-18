package it.adbconstructions.adb_api.service.implementation;

import it.adbconstructions.adb_api.model.Consumer;
import it.adbconstructions.adb_api.model.User;
import it.adbconstructions.adb_api.repository.ConsumerRepository;
import it.adbconstructions.adb_api.service.*;
import org.springframework.stereotype.Service;

import it.adbconstructions.adb_api.common.enumeration.Role;
import it.adbconstructions.adb_api.exception.*;
import it.adbconstructions.adb_api.model.UserPrincipal;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static it.adbconstructions.adb_api.common.constant.UserImplementation.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.*;

@Service
@Transactional
@Qualifier("consumerDetailService")
public class ConsumerService implements IConsumerService, UserDetailsService{
    private Logger LOGGER = LoggerFactory.getLogger(getClass()); //UserServiceImplementation.class
    private ConsumerRepository consumerRepository;

    private BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;
    private StorageService storageService;
    private ResetCodeService resetCodeService;

    @Autowired
    public ConsumerService(ConsumerRepository consumerRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       LoginAttemptService loginAttemptService,
                       EmailService emailService, StorageService storageService, ResetCodeService resetCodeService) {
        this.consumerRepository = consumerRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.storageService = storageService;
        this.resetCodeService = resetCodeService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Consumer user = findUserByUserName(username);
        if (user == null) {
            LOGGER.error(NOT_FOUND_FOR_THE_USERNAME + username);
            throw new UsernameNotFoundException(NOT_FOUND_FOR_THE_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            consumerRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(USER_SUCCESSFULLY_FOUND_BY_THE_USERNAME + username);
            return userPrincipal;
        }
    }

    private void validateLoginAttempt(Consumer user) {
        if (user.isNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempt(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public Consumer register(String firstName, String lastName, String username, String email, String password,String city,String state) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        Consumer user = new Consumer();
        user.setUserId(generateUserId());
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setCity(city);
        user.setState(state);
        user.setEmail(email);
        user.setJoinedDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_CONSUMER.name());
        user.setAuthorities(Role.ROLE_CONSUMER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        consumerRepository.save(user);
        return user;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private Consumer validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        Consumer userByUsername = findUserByUserName(newUsername);
        Consumer userByUEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUsername)) {
            Consumer currentUser = findUserByUserName(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(USER_WAS_NOT_FIND_BY_USERNAME + currentUsername);
            }
            if (userByUsername != null && !currentUser.getId().equals(userByUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByUEmail != null && !currentUser.getId().equals(userByUEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByUEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public List<Consumer> getUsers() {
        return consumerRepository.findAll();
    }

    @Override
    public Consumer findUserByUserName(String username) {
        return consumerRepository.findUserByUsername(username);
    }

    @Override
    public Consumer findUserByEmail(String email) {
        return consumerRepository.findUserByEmail(email);
    }

    @Override
    public Consumer addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        Consumer user = new Consumer();
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinedDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setActive(isActive);
        user.setNotLocked(isNotLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        consumerRepository.save(user);
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public Consumer updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        Consumer currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNotLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        consumerRepository.save(currentUser);
        saveProfileImage(currentUser, profileImage);
        return currentUser;
    }

    @Override
    public Consumer updateUserProfileDetails(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role,String city,String state, boolean isNotLocked, boolean isActive) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        Consumer currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setCity(city);
        currentUser.setState(state);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNotLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        consumerRepository.save(currentUser);
        return currentUser;
    }

    @Override
    public void deleteUser(String username) {
        Consumer user = consumerRepository.findUserByUsername(username);
        consumerRepository.deleteById(user.getId());
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        Consumer user = consumerRepository.findUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        } else {
            String password = generatePassword();
            user.setPassword(encodePassword(password));
            consumerRepository.save(user);
            emailService.sendNewPasswordEmail(user.getFirstName(), password, email);
        }
    }

    @Override
    public Consumer updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF).contains(profileImage.getContentType())) {
            throw new NotAnImageFileException(BAD_REQUEST, profileImage.getOriginalFilename() + " is not an valid image file.");
        }
        Consumer user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public Consumer timeBasedPasswordReset(String username, String code, String password) throws ResetCodeExpiredException {
        Consumer user = consumerRepository.findUserByUsername(username);
        resetCodeService.verify(code);
        user.setPassword(encodePassword(password));
        return consumerRepository.save(user);
    }

    private void saveProfileImage(Consumer user, MultipartFile profileImage) throws IOException, NotAnImageFileException {
        if (profileImage != null) {
            String url = getUploadedUrl(user.getUsername(),profileImage);
            user.setProfileImageUrl(url);
            consumerRepository.save(user);
        } else {
            user.setProfileImageUrl(getTemporaryProfileImageUrl(user.getUsername()));
            consumerRepository.save(user);
        }
    }

    private String getUploadedUrl(String username,MultipartFile multipartFile) throws IOException {
        return storageService.uploadMyProfileImage(username,multipartFile);
    }

    private String setProfileImageUrl(String username) {
        return storageService.getFileUrl(username);
    }

    private String getTemporaryProfileImageUrl(String username) {
        return storageService.getDefaultFileUrl();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

}
