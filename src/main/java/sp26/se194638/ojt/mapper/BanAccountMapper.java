package sp26.se194638.ojt.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sp26.se194638.ojt.model.dto.response.AccountBanResponse;
import sp26.se194638.ojt.model.entity.AccountBan;

@Mapper(componentModel = "spring")
public interface BanAccountMapper {

  @Mapping(source = "bannedBy.username", target = "banBy")
  @Mapping(source = "reason", target = "reason")
  @Mapping(source = "bannedAt", target = "banAt")
  @Mapping(target = "status", constant = "true")
  AccountBanResponse toAccountBanResponse(AccountBan accountBan);
}
