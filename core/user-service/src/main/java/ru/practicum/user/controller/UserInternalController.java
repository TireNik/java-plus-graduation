package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.service.UserInternalService;
import ru.practicum.userClient.user.InternalUserClient;
import ru.practicum.userClient.user.dto.UserDto;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class UserInternalController implements InternalUserClient {

    private final UserInternalService userInternalService;

    @Override
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable Long id) {
        return userInternalService.getUserById(id);
    }

    @Override
    @GetMapping("/{id}/exists")
    @ResponseStatus(HttpStatus.OK)
    public Boolean existsById(@PathVariable Long id) {
        return userInternalService.existsById(id);
    }
}
