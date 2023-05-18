package it.adbconstructions.adb_api.service;

import it.adbconstructions.adb_api.exception.EmailExistException;
import it.adbconstructions.adb_api.exception.NotAnImageFileException;
import it.adbconstructions.adb_api.exception.UserNotFoundException;
import it.adbconstructions.adb_api.exception.UsernameExistException;
import it.adbconstructions.adb_api.model.Employee;
import it.adbconstructions.adb_api.repository.EmployeeRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static it.adbconstructions.adb_api.common.constant.UserImplementation.*;

@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee addNewEmployee(String firstName, String lastName, String username, String email, String role, String phone,String city, String state, String available) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException, MessagingException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        Employee user = new Employee();
        user.setEmployeeId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinedDate(new Date());
        user.setUsername(username);
        user.setCity(city);
        user.setState(state);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAvailable(available);
        user.setRole(role);
        employeeRepository.save(user);
        return user;
    }

    public Employee updateEmployeeProfileDetails(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String phone, String role, String city, String state, String available) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        Employee currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setCity(city);
        currentUser.setState(state);
        currentUser.setEmail(newEmail);
        currentUser.setPhone(phone);
        currentUser.setAvailable(available);
        currentUser.setRole(role);
        employeeRepository.save(currentUser);
        return currentUser;
    }

    private Employee validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        Employee userByUsername = findEmployeeByUserName(newUsername);
        Employee userByUEmail = findEmployeeByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUsername)) {
            Employee currentUser = findEmployeeByUserName(currentUsername);
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

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    public void deleteEmployee(String username) {
        Employee user = employeeRepository.findEmployeeByUsername(username);
        employeeRepository.deleteById(user.getId());
    }

    public Employee findEmployeeByUserName(String username) {
        return employeeRepository.findEmployeeByUsername(username);
    }

    public Employee findEmployeeByEmail(String email) {
        return employeeRepository.findEmployeeByEmail(email);
    }

    public List<String> findUsernamesOfAvailableEmployees()
    {
        return employeeRepository.findUsernamesOfAvailableEmployees();
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }
}
