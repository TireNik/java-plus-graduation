package ru.practicum.userClient.user;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.userClient.user.dto.NewUserRequest;
import ru.practicum.userClient.user.dto.UserDto;

import java.util.List;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserAdminClient {

    @GetMapping
    List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                           @RequestParam(defaultValue = "0") Integer from,
                           @RequestParam(defaultValue = "10") Integer size);

    @PostMapping
    UserDto createUser(@RequestBody @Valid NewUserRequest newUser);

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable Long userId);
}