package ru.practicum.userClient.subscriptions;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.practicum.userClient.subscriptions.dto.SubscriptionDto;

import java.util.List;

@FeignClient(name = "user-service", path = "/users")
public interface SubscriptionPrivateClient {

    @PostMapping("/{userId}/subscriptions/{subscribedToId}")
    SubscriptionDto subscribe(@PathVariable("userId") Long userId,
                              @PathVariable("subscribedToId") Long subscribedToId);

    @DeleteMapping("/{userId}/subscriptions/{subscribedToId}")
    void unsubscribe(@PathVariable("userId") Long userId,
                     @PathVariable("subscribedToId") Long subscribedToId);

    @GetMapping("/{userId}/subscriptions")
    List<SubscriptionDto> getSubscriptions(@PathVariable("userId") Long userId);

    @GetMapping("/{userId}/subscribers")
    List<SubscriptionDto> getSubscribers(@PathVariable("userId") Long userId);
}
