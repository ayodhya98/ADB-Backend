package it.adbconstructions.adb_api.controller;

import it.adbconstructions.adb_api.exception.*;
import it.adbconstructions.adb_api.model.Employee;
import it.adbconstructions.adb_api.model.HttpResponse;
import it.adbconstructions.adb_api.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static it.adbconstructions.adb_api.controller.UserController.DELETED_SUCCESSFULLY;

@RestController
@RequestMapping(value = {"/", "/api/v1/employee"})
public class EmployeeController extends ExceptionHandling {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/add")
    public ResponseEntity<Employee> addEmployee(@RequestParam("firstName") String firstName,
                                            @RequestParam("lastName") String lastName,
                                            @RequestParam("username") String username,
                                            @RequestParam("email") String email,
                                            @RequestParam("role") String role,
                                            @RequestParam("city") String city,
                                            @RequestParam("state") String state,
                                            @RequestParam("available") String available,
                                            @RequestParam("phone") String phone
                                            ) throws UserNotFoundException, EmailExistException, MessagingException, IOException, UsernameExistException, NotAnImageFileException {

        Employee newUser = employeeService.addNewEmployee(firstName,lastName,username,email,role,phone,city,state,available);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/update/profileData")
    public ResponseEntity<Employee> updateEmployee(@RequestParam("currentUsername") String currentUsername,
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("city") String city,
                                           @RequestParam("state") String state,
                                           @RequestParam("available") String available,
                                           @RequestParam("phone") String phone) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {

        Employee updatedUser = employeeService.updateEmployeeProfileDetails(currentUsername,firstName,lastName,username,email,phone,role,city,state,available);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<Employee> getEmployee(@PathVariable("username") String username){
        Employee user = employeeService.findEmployeeByUserName(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Employee>> getAllEmployees(){
        List<Employee> users = employeeService.getEmployees();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteEmployee(@PathVariable("username") String username) throws IOException {
        employeeService.deleteEmployee(username);
        return response(HttpStatus.OK, DELETED_SUCCESSFULLY);
    }

    @GetMapping("/names/list")
    public ResponseEntity<List<String>> getUsernamesList() throws MessagingException {
        List<String> usernames = employeeService.findUsernamesOfAvailableEmployees();
        return new ResponseEntity<>(usernames, HttpStatus.OK);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }
}
