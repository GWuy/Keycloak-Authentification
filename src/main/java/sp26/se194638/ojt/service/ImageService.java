package sp26.se194638.ojt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import sp26.se194638.ojt.annotation.Audit;
import sp26.se194638.ojt.exception.BusinessException;
import sp26.se194638.ojt.model.dto.response.ImageResponse;
import sp26.se194638.ojt.model.entity.Image;
import sp26.se194638.ojt.model.entity.User;
import sp26.se194638.ojt.model.enums.AuditAction;
import sp26.se194638.ojt.model.enums.ErrorCode;
import sp26.se194638.ojt.repository.ImageRepository;
import sp26.se194638.ojt.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ImageService {

  @Autowired
  private S3Client s3Client;

  @Autowired
  private ImageRepository imageRepository;

  @Value("${cloudflare.r2.bucket-name}")
  private String bucketName;

  @Value("${cloudflare.r2.public-base-url}")
  private String publicBaseUrl;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private RedisService redisService;

  @Autowired
  private UserRepository userRepository;

  @Audit(action = AuditAction.UPLOAD_IMAGE)
  public ImageResponse upload(
    MultipartFile file,
    String folder,
    String header
  ) {

    // 1. Validate token
    if (header == null || !header.startsWith("Bearer ")) {
      throw new BusinessException(
        ErrorCode.INVALID_TOKEN,
        "Invalid authorization header",
        AuditAction.UPLOAD_IMAGE
      );
    }

    String token = header.substring(7);

    if (!redisService.isTokenValid(jwtService.extractJwId(token), token)) {
      throw new BusinessException(
        ErrorCode.INVALID_TOKEN,
        "Token expired or invalid",
        AuditAction.UPLOAD_IMAGE
      );
    }

    // 2. Validate file
    if (file == null || file.isEmpty()) {
      throw new BusinessException(
        ErrorCode.INVALID_REQUEST,
        "File is empty",
        AuditAction.UPLOAD_IMAGE
      );
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.matches("image/(png|jpeg|webp)")) {
      throw new BusinessException(
        ErrorCode.INVALID_REQUEST,
        "Unsupported image type",
        AuditAction.UPLOAD_IMAGE
      );
    }

    // 3. Get user
    String username = jwtService.extractUsername(token);
    User user = userRepository.findByUsername(username);

    // 4. Build object key
    String fileKey = folder + "/"
      + user.getId() + "/"
      + UUID.randomUUID() + "-" + file.getOriginalFilename();

    try {
      // 5. Upload to R2
      s3Client.putObject(
        PutObjectRequest.builder()
          .bucket(bucketName)
          .key(fileKey)
          .contentType(contentType)
          .build(),
        RequestBody.fromBytes(file.getBytes())
      );

      String fileUrl = publicBaseUrl + "/" + fileKey;

      // 6. Save DB
      Image image = Image.builder()
        .user(user)
        .fileUrl(fileUrl)
        .createdAt(LocalDateTime.now())
        .build();

      imageRepository.save(image);

      return ImageResponse.builder()
        .id(image.getId())
        .url(fileUrl)
        .build();

    } catch (IOException e) {
      throw new BusinessException(
        ErrorCode.INTERNAL_ERROR,
        "Upload image failed",
        AuditAction.UPLOAD_IMAGE
      );
    }
  }
}
