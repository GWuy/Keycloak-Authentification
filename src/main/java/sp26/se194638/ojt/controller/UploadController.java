package sp26.se194638.ojt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sp26.se194638.ojt.model.dto.response.GlobalResponse;
import sp26.se194638.ojt.service.KycService;
import sp26.se194638.ojt.service.UploadService;
import sp26.se194638.ojt.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("assign1/api/images")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class UploadController {

  @Autowired
  private UploadService uploadService;

  @Autowired
  private UserService userService;

  @Autowired
  private KycService kycService;

  @PostMapping("/upload-avatar")
  public ResponseEntity<?> uploadAvatar(
    @RequestHeader("Authorization") String header,
    @RequestParam("avatar") MultipartFile avatarFile) {
    return ResponseEntity.ok(userService.uploadAvatar(header, avatarFile));
  }

  @PostMapping("/upload-kyc-image/{kycId}")
  public ResponseEntity<GlobalResponse> uploadKycImage(
    @PathVariable Integer kycId,
    @RequestHeader("Authorization") String header,
    @RequestParam("frontImage") MultipartFile frontImageFile,
    @RequestParam("backImage") MultipartFile backImageFile
  ) {
    GlobalResponse response = kycService.uploadKycImage(kycId, frontImageFile, backImageFile, header);
    return ResponseEntity.ok(response);
  }
}
