package sp26.se194638.ojt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sp26.se194638.ojt.model.dto.response.ImageResponse;
import sp26.se194638.ojt.service.ImageService;

@RestController
@RequestMapping("assign1/api/images")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class ImageController {

  @Autowired
  private ImageService imageService;


  @PostMapping("/upload/avatar")
  public ImageResponse uploadAvatar(
    @RequestParam MultipartFile file,
    @RequestHeader("Authorization") String header
  ) {
    return imageService.upload(file, "avatar", header);
  }

  @PostMapping("/upload/kyc")
  public ImageResponse uploadKycImage(
    @RequestParam MultipartFile file,
    @RequestHeader("Authorization") String header
  ) {
    return imageService.upload(file, "image-id", header);
  }
}
