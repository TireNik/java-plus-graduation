package ru.practicum.userClient.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.userClient.user.dto.NewUserRequest;
import ru.practicum.userClient.user.dto.UserDto;

import java.util.List;

@FeignClient(name = "user-service", contextId = "userAdminClient", path = "/admin/users")
public interface UserAdminClient {

    @GetMapping
    List<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size);

    @PostMapping
    UserDto createUser(@RequestBody @Valid NewUserRequest newUser);

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable @Positive Long userId);
}