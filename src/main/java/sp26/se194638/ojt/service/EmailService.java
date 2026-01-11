package sp26.se194638.ojt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  @Autowired
  private JavaMailSender mailSender;

  public void sendOtpEmail(String to, String otp) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("Your OTP Code");
    message.setText(
      "Dear user,\n\n" +
        "Your OTP code is: " + otp + "\n" +
        "It is valid for 5 minutes.\n\n" +
        "If you did not request this code, please ignore this email.\n\n" +
        "Best regards,\n" +
        "Your Company"
    );
    message.setFrom("voltera.global@gmail.com");
    mailSender.send(message);
    System.out.println("Sent OTP to " + to + ": " + otp);
  }


//  public void sendContractEmailWithAttachment(Contract contract, MultipartFile file) {
//    try {
//      MimeMessage message = mailSender.createMimeMessage();
//      MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//      helper.setTo(new String[]{
//        contract.getBuyerid().getEmail(),
//        contract.getSellerid().getEmail()
//      });
//      helper.setSubject("Contract Signed - #" + contract.getId());
//      helper.setText("""
//                Dear users,
//
//                Your contract has been signed by both parties.
//                Please find the attached PDF copy for your reference.
//
//                Best regards,
//                Voltera Team
//            """);
//
//      helper.addAttachment("Contract_" + contract.getId() + ".pdf",
//        new ByteArrayResource(file.getBytes()));
//
//      mailSender.send(message);
//    } catch (Exception e) {
//      throw new RuntimeException("Failed to send contract email", e);
//    }
//  }
}
