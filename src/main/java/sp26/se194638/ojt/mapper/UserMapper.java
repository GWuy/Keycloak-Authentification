package sp26.se194638.ojt.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sp26.se194638.ojt.model.entity.User;
import sp26.se194638.ojt.model.dto.response.ProfileResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(source = "user.firstName", target = "firstname")
  @Mapping(source = "user.lastName", target = "lastname")
  @Mapping(source = "user.avatar", target = "avatar")
  ProfileResponse toProfileResponse(User user);
}
