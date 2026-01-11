package sp26.se194638.ojt.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class R2Config {

  @Value("${cloudflare.r2.access-key-id}")
  private String accessKey;

  @Value("${cloudflare.r2.secret-key-id}")
  private String secretKey;

  @Value("${cloudflare.r2.end-point}")
  private String endpoint;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
      .endpointOverride(URI.create(endpoint))
      .credentialsProvider(
        StaticCredentialsProvider.create(
          AwsBasicCredentials.create(accessKey, secretKey)
        )
      )
      .region(Region.US_EAST_1)

      // 🔴 CẤU HÌNH QUYẾT ĐỊNH CHO R2
      .serviceConfiguration(
        software.amazon.awssdk.services.s3.S3Configuration.builder()
          .pathStyleAccessEnabled(true)
          .checksumValidationEnabled(false)
          .chunkedEncodingEnabled(false)
          .build()
      )

      // ❗ KHÔNG override signer
      .build();
  }
}
