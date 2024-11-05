package com.kyawgyi.role.controller;

import com.kyawgyi.role.dto.MailBody;
import com.kyawgyi.role.model.ForgetPassword;
import com.kyawgyi.role.model.Users;
import com.kyawgyi.role.repo.ForgetPasswordRepo;
import com.kyawgyi.role.repo.UsersRepo;
import com.kyawgyi.role.service.EmailService;
import com.kyawgyi.role.utils.ChangePassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
public class ForgetPasswordController {

    private final UsersRepo usersRepo;
    private final EmailService emailService;
    private final ForgetPasswordRepo forgetPasswordRepo;
    private final PasswordEncoder passwordEncoder;

    public ForgetPasswordController(UsersRepo usersRepo, EmailService emailService,
                                    ForgetPasswordRepo forgetPasswordRepo,
                                    PasswordEncoder passwordEncoder) {
        this.usersRepo = usersRepo;
        this.emailService = emailService;
        this.forgetPasswordRepo = forgetPasswordRepo;
        this.passwordEncoder = passwordEncoder;
    }

    //    send mail for email valid
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyMail(@PathVariable String email) {
        Users user = usersRepo.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Please provide a valid Email!"));

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .body("This is OTP for forget Password Request: "+ otp)
                .subject("OTP for Forget Password Request")
                .build();

        ForgetPassword fp = ForgetPassword.builder()
                .otp(otp)
                .expiryDate(new Date(System.currentTimeMillis() + 60 * 1000))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgetPasswordRepo.save(fp);
        return  ResponseEntity.ok("Email Send for verification successful : " + otp);

    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOTP(@PathVariable Integer otp, @PathVariable String email) {
        Users user = usersRepo.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Please provide a valid Email!"));

        ForgetPassword fp=forgetPasswordRepo.findByOtpAndUser(otp,user)
                .orElseThrow(()->new RuntimeException("Please provide a valid OTP!"));

        if(fp.getExpiryDate().before(Date.from(Instant.now()))){
            forgetPasswordRepo.deleteById(fp.getId());
            return new  ResponseEntity<>("OTP is expired",HttpStatus.EXPECTATION_FAILED);
        }

//        delete otp
        forgetPasswordRepo.deleteById(fp.getId());


        return  ResponseEntity.ok("OTP is valid");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePassword(@PathVariable String email,
                                                 @RequestBody ChangePassword changePassword) {

        if(!Objects.equals(changePassword.password(),changePassword.confirmPassword())){
            return new ResponseEntity<>("Please Enter the password Again!",HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        usersRepo.updatePassword(email,encodedPassword);

        Users user = usersRepo.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found with this email!"));

        forgetPasswordRepo.findByUser(user).ifPresent(forgetPassword -> {
            forgetPasswordRepo.deleteById(forgetPassword.getId());
        });

        return ResponseEntity.ok("Password changed successfully");
    }

    private Integer otpGenerator(){
        Random random= new Random();
        return random.nextInt(100000,999999);
    }
}
