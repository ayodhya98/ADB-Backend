package it.adbconstructions.adb_api.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.adbconstructions.adb_api.exception.*;
import it.adbconstructions.adb_api.model.HttpResponse;
import it.adbconstructions.adb_api.model.Request;
import it.adbconstructions.adb_api.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"/", "/request"})
public class RequestController extends ExceptionHandling {

    @Autowired
    private RequestService requestService;

    @PostMapping("/create/{username}")
    public ResponseEntity<Request> createRequest(@PathVariable("username") String username, @RequestBody ObjectNode content) throws AlreadyActiveRequestFoundException, AlreadyActiveRoomFoundException {
        Request newRequest = requestService.createRequest(username,content.get("content").asText());
        return new ResponseEntity<>(newRequest, HttpStatus.OK);
    }
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<List<Request>> getRequestsByAdmin() throws AlreadyActiveRequestFoundException, AlreadyActiveRoomFoundException {
        List<Request> requests = requestService.getRequestsByAdmin();
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @GetMapping("/status/{username}")
    public ResponseEntity<Boolean> createRequest(@PathVariable("username") String username) throws AlreadyActiveRequestFoundException, AlreadyActiveRoomFoundException {
        Boolean status = requestService.getStatus(username);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PutMapping("/assign/{username}/{requestCode}")
    public ResponseEntity<Request> assignConsultant(@PathVariable("username") String username,@PathVariable("requestCode") String requestCode, @RequestParam("consultantUsername") String consultantUsername) throws RequestNotFoundException {
        Request newRequest = requestService.assignConsultant(username,consultantUsername,requestCode);
        return new ResponseEntity<>(newRequest, HttpStatus.OK);
    }

    @PutMapping("/update/{username}/{requestCode}")
    public ResponseEntity<Request> assignConsultant(@PathVariable("username") String username,@PathVariable("requestCode") String requestCode, @RequestParam("isFinished") Boolean isFinished) throws RequestNotFoundException {
        Request newRequest = requestService.updateStatus(username,isFinished,requestCode);
        return new ResponseEntity<>(newRequest, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{username}/{requestCode}")
    public ResponseEntity<HttpResponse> deleteRequest(@PathVariable("username") String username,@PathVariable("requestCode") String requestCode) throws AlreadyActiveRequestFoundException, RequestNotFoundException {
        requestService.deleteRequest(username,requestCode);
        return response(HttpStatus.OK, "Request successfully deleted");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }
}
