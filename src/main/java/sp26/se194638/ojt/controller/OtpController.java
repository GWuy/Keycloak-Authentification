package sp26.se194638.ojt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sp26.se194638.ojt.exception.GlobalException;
import sp26.se194638.ojt.model.dto.request.OtpRequest;
import sp26.se194638.ojt.model.dto.request.PasswordResetRequest;
import sp26.se194638.ojt.model.dto.request.RequestChangPasswordOtp;
import sp26.se194638.ojt.service.OtpService;
import sp26.se194638.ojt.service.UserService;

@RestController
@RequestMapping("assign1/api/otp")
public class OtpController {
  @Autowired
  private OtpService otpService;

  @Autowired
  private UserService userService;


  @PostMapping("/forgot/reset")
  public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
    userService.updatePassword(request.getEmail(), request.getNewPassword());
    return ResponseEntity.ok("Password reset successfully");
  }

  @PostMapping("/request")
  public ResponseEntity<String> requestOtp(@RequestParam String email) {
    try {
      userService.isEmailExist(email);
      return ResponseEntity.badRequest().body("This email is already registered");
    } catch(GlobalException e) {
      System.out.printf(e.getMessage());
    }
    otpService.generateOtp(email);
    return ResponseEntity.ok("OTP has been sent to " + email);
  }

  @PostMapping("/verify")
  public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest request) {
    try {
      userService.verifyRegisterOtp(request.getEmail(), request.getOtp());
      return ResponseEntity.ok("Email verified successfully");
    } catch(GlobalException e){
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/verify/register")
  public ResponseEntity<?> verifyRegisterOtp(@RequestBody OtpRequest request) {
    try {
      if (!otpService.verifyOtp(request.getEmail(), request.getOtp())) {
        return ResponseEntity.badRequest().body("OTP invalid or expired");
      }
      return ResponseEntity.ok("OTP verified successfully");
    } catch(GlobalException e){
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/resend")
  public ResponseEntity<String> resendOtp(@RequestParam String email) {
    otpService.resendOtp(email);
    return ResponseEntity.ok("OTP resent to " + email);
  }

  @PostMapping("/forgot/request")
  public ResponseEntity<String> requestPasswordOtp(@RequestBody RequestChangPasswordOtp request) {
    if (userService.isEmailExist(request.getEmail())) {
      return ResponseEntity.badRequest().body("This email isn't exist");
    }
    otpService.generateOtp(request.getEmail());
    return ResponseEntity.ok("OTP for password reset sent to " + request.getEmail());
  }

  @PostMapping("/forgot/verify")
  public ResponseEntity<?> verifyPasswordOtp(@RequestBody PasswordResetRequest request) {
    boolean valid = otpService.verifyOtp(request.getEmail(), request.getOtp());
    if(valid) {
      userService.updatePassword(request.getEmail(), request.getNewPassword());
      return ResponseEntity.ok("Password reset successfully");
    } else {
      return ResponseEntity.badRequest().body("Invalid or expired OTP");
    }
  }
}
