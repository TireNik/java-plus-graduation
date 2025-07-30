package ru.practicum.subscriptions.service;

import ru.practicum.userClient.subscriptions.dto.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDto subscribe(Long subscriberId, Long subscribedToId);

    void unsubscribe(Long subscriberId, Long subscribedToId);

    List<SubscriptionDto> getSubscriptions(Long userId);

    List<SubscriptionDto> getSubscribers(Long userId);
}
