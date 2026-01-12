package sp26.se194638.ojt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sp26.se194638.ojt.exception.GlobalException;
import sp26.se194638.ojt.mapper.KycMapper;
import sp26.se194638.ojt.model.dto.request.KycRegisterRequest;
import sp26.se194638.ojt.model.dto.response.GlobalResponse;
import sp26.se194638.ojt.model.dto.response.ListKycResponse;
import sp26.se194638.ojt.model.entity.Kyc;
import sp26.se194638.ojt.model.entity.User;
import sp26.se194638.ojt.model.enums.AuditAction;
import sp26.se194638.ojt.model.enums.ErrorCode;
import sp26.se194638.ojt.repository.KycRepository;
import sp26.se194638.ojt.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class KycService {
  @Autowired
  private KycRepository kycRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RedisService redisService;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UserService userService;

  @Autowired
  private KycMapper kycMapper;

  @Autowired
  private UploadService uploadService;

  public GlobalResponse registerIdentificationCard(KycRegisterRequest request, String header) {

    if (header == null || !header.startsWith("Bearer ")) {
      throw new GlobalException(ErrorCode.INVALID_HEADER, "Invalid header", AuditAction.UPLOAD_KYC);
    }

    String token = header.substring(7);
    if (!redisService.isTokenValid(jwtService.extractJwId(token), token)) {
      throw new GlobalException(ErrorCode.INVALID_TOKEN, "Token invalid or expired", AuditAction.UPLOAD_KYC);
    }

    User userRegisKyc = userRepository.findByUsername(jwtService.extractUsername(token));
    if (userRegisKyc == null) {
      throw new GlobalException(ErrorCode.USER_NOT_FOUND, "User not found", AuditAction.UPLOAD_KYC);
    }

    if (kycRepository.existsByNo(request.getNo())) {
      throw new GlobalException(ErrorCode.EXIST_ID_NUMBER, "ID Number was existed", AuditAction.UPLOAD_KYC);
    }

    Kyc kyc = Kyc.builder()
      .user(userRegisKyc)
      .no(request.getNo())
      .fullName(request.getFullName().trim())
      .dateOfBirth(request.getDateOfBirth())
      .sex(request.getSex())
      .nationality(request.getNationality())
      .placeOfOrigin(request.getPlaceOfOrigin().trim())
      .dateOfExpiry(request.getDateOfExpiry())
      .dateIssue(request.getDateIssue())
      .placeOfIssue(request.getPlaceOfIssue().trim())
      .status("PENDING")
      .submittedAt(LocalDateTime.now())
      .build();

    kycRepository.save(kyc);

    return GlobalResponse.builder()
      .message("Add new Id successfully")
      .isSuccess(true)
      .build();
  }

  public GlobalResponse updateIdentificationCardStatus(Integer idCard, String header, String status) {
    if (!userService.isAdmin(header)) {
      throw new GlobalException(ErrorCode.FORBIDDEN, "You don't have permission to access this resource", AuditAction.OVERVIEW);
    }

    Kyc kyc = kycRepository.findKycById(idCard);

    if (kyc == null) {
      throw  new GlobalException(ErrorCode.KYC_NOT_FOUND, "Identification Card not found", AuditAction.APPROVE_KYC);
    }

    kyc.setStatus(status.toUpperCase());
    kyc.setVerifiedAt(LocalDateTime.now());
    kycRepository.save(kyc);

    return GlobalResponse.builder()
      .isSuccess(true)
      .message(status + " Identity Card with id: " + idCard + " successfully")
      .build();
  }

  public List<ListKycResponse> listAllIdCards(String header) {
    if (!userService.isAdmin(header)) {
      throw new GlobalException(ErrorCode.FORBIDDEN, "You don't have permission to access this resource", AuditAction.LIST_KYC);
    }
    return kycRepository.findAll().stream().map(kycMapper::toListKycResponse).toList();
  }

  public ListKycResponse getIdCardByUser(String header) {

    if (header == null || header.startsWith("Bearer ")) {
      throw  new GlobalException(ErrorCode.INVALID_HEADER, "Invalid header or null", AuditAction.LIST_KYC);
    }

    String token = header.substring(7);
    if (redisService.isTokenValid(jwtService.extractJwId(token), token)) {
      throw  new GlobalException(ErrorCode.INVALID_TOKEN, "Token invalid or expired", AuditAction.LIST_KYC);
    }

    User owner = userRepository.findByUsername(jwtService.extractUsername(token));
    if (owner== null) {
      throw  new GlobalException(ErrorCode.USER_NOT_FOUND, "User not found", AuditAction.LIST_KYC);
    }

    return kycRepository.findKycByUser(owner);
  }

  public GlobalResponse uploadKycImage(
    Integer kycId,
    MultipartFile frontImage,
    MultipartFile backImage,
    String header
  ) {
    if (header == null || !header.startsWith("Bearer ")) {
      throw new GlobalException(ErrorCode.INVALID_HEADER, "Invalid header", AuditAction.UPLOAD_KYC);
    }

    String token = header.substring(7);
    if (!redisService.isTokenValid(jwtService.extractJwId(token), token)) {
      throw new GlobalException(ErrorCode.INVALID_TOKEN, "Token invalid or expired", AuditAction.UPLOAD_KYC);
    }

    User user = userRepository.findByUsername(jwtService.extractUsername(token));
    if (user == null) {
      throw new GlobalException(ErrorCode.USER_NOT_FOUND, "User not found", AuditAction.UPLOAD_KYC);
    }

    Kyc kyc = kycRepository.findKycById(kycId);
    if (kyc == null) {
      throw new GlobalException(ErrorCode.KYC_NOT_FOUND, "KYC not found", AuditAction.UPLOAD_KYC);
    }

    if (!Objects.equals(user.getId(), kyc.getUser().getId())) {
      throw new GlobalException(ErrorCode.FORBIDDEN, "You don't have permission", AuditAction.UPLOAD_KYC);
    }

    String frontImageUrl = uploadService.upload(frontImage, "image-id");
    String backImageUrl = uploadService.upload(backImage, "image-id");

    kyc.setFrontImage(frontImageUrl);
    kyc.setBackImage(backImageUrl);
    kycRepository.save(kyc);

    return GlobalResponse.builder()
      .message("Upload KYC images successfully")
      .isSuccess(true)
      .build();
  }
}
