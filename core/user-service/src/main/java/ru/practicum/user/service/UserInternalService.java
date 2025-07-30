package ru.practicum.user.service;

import ru.practicum.userClient.user.dto.UserDto;

public interface UserInternalService {
    UserDto getUserById(Long id);

    Boolean existsById(Long id);
}
