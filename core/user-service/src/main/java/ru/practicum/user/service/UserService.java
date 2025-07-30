package ru.practicum.user.service;

import ru.practicum.userClient.user.dto.NewUserRequest;
import ru.practicum.userClient.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto create(NewUserRequest newUser);

    void delete(Long id);

}