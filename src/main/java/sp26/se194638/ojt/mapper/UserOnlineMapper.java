package sp26.se194638.ojt.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sp26.se194638.ojt.model.dto.response.UserLoggingResponse;
import sp26.se194638.ojt.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserOnlineMapper {

  @Mapping(target = "id", source = "id")
  @Mapping(target = "username", source = "username")
  @Mapping(
    target = "fullName",
    expression = "java(user.getLastName() + \" \" + user.getFirstName())"
  )
  @Mapping(target = "email", source = "email")
  @Mapping(target = "role", source = "role")
  UserLoggingResponse toOnUserLoggingResponse(User user);
}

