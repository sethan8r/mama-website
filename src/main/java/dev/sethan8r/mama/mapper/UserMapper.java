package dev.sethan8r.mama.mapper;

import dev.sethan8r.mama.dto.UserResponseDto;
import dev.sethan8r.mama.model.User;
import dev.sethan8r.mama.security.CustomUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);

    @Mapping(target = "authorities", ignore = true)
    CustomUserDetails toCustomUserDetails(User user);
}
