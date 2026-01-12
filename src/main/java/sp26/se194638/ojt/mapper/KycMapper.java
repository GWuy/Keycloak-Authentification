package sp26.se194638.ojt.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sp26.se194638.ojt.model.dto.response.ListKycResponse;
import sp26.se194638.ojt.model.entity.Kyc;

@Mapper(componentModel = "spring")
public interface KycMapper {

  @Mapping(target = "owner", source = "user.username")
  @Mapping(target = "idNumber", source = "no")
  @Mapping(target = "frontImage", source = "frontImage")
  @Mapping(target = "backImage", source = "backImage")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "submittedAt", source = "submittedAt")
  @Mapping(target = "verifiedAt", source = "verifiedAt")
  ListKycResponse toListKycResponse(Kyc kyc);
}
