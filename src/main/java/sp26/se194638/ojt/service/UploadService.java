package sp26.se194638.ojt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import sp26.se194638.ojt.annotation.Audit;
import sp26.se194638.ojt.exception.GlobalException;
import sp26.se194638.ojt.model.enums.AuditAction;
import sp26.se194638.ojt.model.enums.ErrorCode;

import java.io.IOException;
import java.util.UUID;

@Service
public class UploadService {

  @Autowired
  private S3Client s3Client;

  @Value("${cloudflare.r2.bucket-name}")
  private String bucketName;

  @Value("${cloudflare.r2.public-base-url}")
  private String publicBaseUrl;

  @Audit(action = AuditAction.UPLOAD_IMAGE)
  public String upload(
    MultipartFile file,
    String folder
  ) {

    // 2. Validate file
    if (file == null || file.isEmpty()) {
      throw new GlobalException(
        ErrorCode.INVALID_REQUEST,
        "File is empty",
        AuditAction.UPLOAD_IMAGE
      );
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.matches("image/(png|jpeg|webp)")) {
      throw new GlobalException(
        ErrorCode.INVALID_REQUEST,
        "Unsupported image type",
        AuditAction.UPLOAD_IMAGE
      );
    }

    // 4. Build object key
    String fileKey = folder + "/"
      + UUID.randomUUID() + "-" + file.getOriginalFilename();

    try {
      // 5. Upload to R2
      s3Client.putObject(
        PutObjectRequest.builder()
          .bucket(bucketName)
          .key(fileKey)
          .contentType(contentType)
          .acl(ObjectCannedACL.PUBLIC_READ)
          .build(),
        RequestBody.fromBytes(file.getBytes())
      );

      return publicBaseUrl + "/" + fileKey;

    } catch (IOException e) {
      throw new GlobalException(
        ErrorCode.INTERNAL_ERROR,
        "Upload image failed",
        AuditAction.UPLOAD_IMAGE
      );
    }
  }
}
