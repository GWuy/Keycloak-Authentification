package sp26.se194638.ojt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;


  @Autowired
  private EmailService emailService;
  public void generateOtp(String email) {
    String otp = String.valueOf(new Random().nextInt(899999) + 100000); // OTP 6 số
    String key = "OTP:" + email;

    redisTemplate.opsForValue().set(key, otp, 5, TimeUnit.MINUTES);


    emailService.sendOtpEmail(email, otp);
  }


  public boolean verifyOtp(String email, String otp) {
    String key = "OTP:" + email;
    String value = redisTemplate.opsForValue().get(key);

    if (value != null && value.equals(otp)) {
      redisTemplate.delete(key);
      return true;
    }
    return false;
  }
  public void resendOtp(String email) {
    generateOtp(email);
  }
}
