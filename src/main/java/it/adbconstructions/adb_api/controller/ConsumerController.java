package it.adbconstructions.adb_api.controller;

import it.adbconstructions.adb_api.exception.*;
import it.adbconstructions.adb_api.model.Consumer;
import it.adbconstructions.adb_api.model.HttpResponse;
import it.adbconstructions.adb_api.model.UserPrincipal;
import it.adbconstructions.adb_api.service.IConsumerService;
import it.adbconstructions.adb_api.service.ResetCodeService;
import it.adbconstructions.adb_api.utility.JWTTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static it.adbconstructions.adb_api.common.constant.Security.JWT_TOKEN_HEADER;

@RestController
@RequestMapping(value = {"/", "/api/v1/consumer"})
public class ConsumerController extends ExceptionHandling {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    public static final String EMAIL_SENT = "New password was sent to the email: ";
    public static final String DELETED_SUCCESSFULLY = "User deleted successfully";
    private IConsumerService consumerService;

    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;
    private ResetCodeService resetCodeService;


    @Autowired
    public ConsumerController(IConsumerService consumerService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider,ResetCodeService resetCodeService) {
        this.consumerService = consumerService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.resetCodeService = resetCodeService;
    }

    @PostMapping("/login")
    public ResponseEntity<Consumer> login(@RequestBody Consumer user){
        authenticate(user.getUsername(), user.getPassword());
        Consumer loginUser = consumerService.findUserByUserName(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeaders(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Consumer> register(@RequestBody Consumer user) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        Consumer newUser = consumerService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getPassword(), user.getCity(),user.getState());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Consumer> addUser(@RequestParam("firstName") String firstName,
                                        @RequestParam("lastName") String lastName,
                                        @RequestParam("username") String username,
                                        @RequestParam("email") String email,
                                        @RequestParam("role") String role,
                                        @RequestParam("isActive") String isActive,
                                        @RequestParam("isNotLocked") String isNotLocked,
                                        @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException, NotAnImageFileException {

        Consumer newUser = consumerService.addNewUser(firstName,lastName, username,email,role,Boolean.parseBoolean(isNotLocked),
                Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Consumer> updateUser(@RequestParam("currentUsername") String currentUsername,
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNotLocked") String isNotLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {

        Consumer updatedUser = consumerService.updateUser(currentUsername,firstName,lastName, username,email,role,Boolean.parseBoolean(isNotLocked),
                Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PostMapping("/update/profileData")
    public ResponseEntity<Consumer> updateUser(@RequestParam("currentUsername") String currentUsername,
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("city") String city,
                                           @RequestParam("state") String state,
                                           @RequestParam(name = "role", defaultValue = "ROLE_CONSUMER") String role,
                                           @RequestParam(name = "isActive", defaultValue = "true") String isActive,
                                           @RequestParam(name = "isNotLocked", defaultValue = "true") String isNotLocked) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {

        Consumer updatedUser = consumerService.updateUserProfileDetails(currentUsername,firstName,lastName, username,email,role,city,state,Boolean.parseBoolean(isNotLocked),
                Boolean.parseBoolean(isActive));
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<Consumer> getUser(@PathVariable("username") String username){
        Consumer user = consumerService.findUserByUserName(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Consumer>> getAllUsers(){
        List<Consumer> users = consumerService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
        consumerService.resetPassword(email);
        return response(HttpStatus.OK, EMAIL_SENT + email);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        consumerService.deleteUser(username);
        return response(HttpStatus.OK, DELETED_SUCCESSFULLY);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<Consumer> updateProfileImage(@RequestParam("username") String username, @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {
        Consumer user = consumerService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/password/reset/{username}")
    public ResponseEntity<Consumer> resetByTime(@PathVariable("username") String username, @RequestParam("code") String code, @RequestParam("password") String password) throws ResetCodeExpiredException {
        Consumer user = consumerService.timeBasedPasswordReset(username,code,password);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/password/code/{username}")
    public ResponseEntity<HttpResponse> getCode(@PathVariable("username") String username, @RequestParam("email") String email) throws MessagingException {
        resetCodeService.generateCode(username,email);
        return response(HttpStatus.OK, "Code sent to the email: " + email);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }

    private HttpHeaders getJwtHeaders(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
