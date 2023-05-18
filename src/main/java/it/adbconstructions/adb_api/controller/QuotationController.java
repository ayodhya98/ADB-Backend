package it.adbconstructions.adb_api.controller;

import it.adbconstructions.adb_api.model.Quotation;
import it.adbconstructions.adb_api.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping(value = {"/", "/api/v1/quotation"})
public class QuotationController {

    @Autowired
    private MailService mailService;

    @PostMapping("/generate")
    public ResponseEntity<String> generate(@RequestBody Quotation quotation, @RequestParam("email") String email){

        // Send the email with the Quotation data
        try {
            mailService.sendEmailWithTemplate(email, "Quotation Summary", "quotation-template", quotation);
        } catch (MessagingException e) {
            return new ResponseEntity<>("Failed to send email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok("Email sent successfully");
    }
}
