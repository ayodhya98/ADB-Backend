package it.adbconstructions.adb_api.service;

import it.adbconstructions.adb_api.exception.ResetCodeExpiredException;
import it.adbconstructions.adb_api.model.ResetCode;
import it.adbconstructions.adb_api.repository.ResetCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@EnableScheduling
public class ResetCodeService {

    @Autowired
    private ResetCodeRepository resetCodeRepository;
    @Autowired
    private EmailService emailService;

    public ResetCode generateCode(String username,String email) throws MessagingException {
        ResetCode newCode = new ResetCode();
        newCode.setCode(username + generateResetCode());
        newCode.setGeneratedTime(new Date());
        emailService.sendResetCode(username,newCode.getCode(),email);
        return resetCodeRepository.save(newCode);
    }

    private String generateResetCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // generate a random 6-digit number
        return String.valueOf(code);
    }

    public ResetCode verify(String code) throws ResetCodeExpiredException {
        ResetCode resetCode = resetCodeRepository.findByCode(code);
        if (resetCode != null)
        {
            return resetCode;
        }
        else
        {
            throw new ResetCodeExpiredException("Reset Code you entered has expired/invalid");
        }
    }

    @Scheduled(fixedDelay = 60000) // Run every 60 seconds
    public void removeExpiredItems() {
        for (ResetCode code : resetCodeRepository.findAll()) {
            Date resetCodeTimestamp = code.getGeneratedTime();
            Date now = new Date();
            long diffInMillis = now.getTime() - resetCodeTimestamp.getTime();
            long diffInMinutes = TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS);
            if (diffInMinutes > 2) {
                resetCodeRepository.delete(code);
            }
        }
    }
}
