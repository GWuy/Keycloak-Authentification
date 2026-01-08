package sp26.se194638.ojt.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sp26.se194638.ojt.model.entity.AuditBacklog;
import sp26.se194638.ojt.model.dto.response.AuditBacklogResponse;

@Mapper(componentModel = "spring")
public interface AuditBacklogMapper {

  @Mapping(source = "actor.username", target = "username")
  @Mapping(source = "actionAt", target = "actionAt")
  AuditBacklogResponse toAuditBacklogResponse(AuditBacklog auditBacklog);
}
