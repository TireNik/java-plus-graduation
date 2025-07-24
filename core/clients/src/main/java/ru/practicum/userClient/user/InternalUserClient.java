package ru.practicum.userClient.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.userClient.user.dto.UserDto;

@FeignClient(name = "user-service", path = "/internal/users")
public interface InternalUserClient {

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable("id") Long id);

    @GetMapping("/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);
}
