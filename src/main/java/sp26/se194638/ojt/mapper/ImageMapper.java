package sp26.se194638.ojt.mapper;

import org.mapstruct.Mapper;
import sp26.se194638.ojt.model.dto.response.ImageResponse;
import sp26.se194638.ojt.model.entity.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
  ImageResponse toResponse(Image image);
}
