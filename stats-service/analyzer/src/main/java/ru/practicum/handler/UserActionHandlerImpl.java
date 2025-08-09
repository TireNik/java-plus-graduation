package ru.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.ActionType;
import ru.practicum.model.UserAction;
import ru.practicum.repository.UserActionRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActionHandlerImpl implements UserActionHandler {
    private final UserActionRepository userActionRepository;

    @Value("${application.action-weight.view}")
    private double view;
    @Value("${application.action-weight.register}")
    private double register;
    @Value("${application.action-weight.like}")
    private double like;

    @Transactional
    @Override
    public void handle(UserActionAvro avro) {
        log.info("Сохранение действия пользователя: {}", avro);
        Optional<UserAction> userActionOpt = userActionRepository.findByUserIdAndEventId(avro.getUserId(),
                avro.getEventId());

        if (userActionOpt.isPresent()) {
            UserAction userAction = userActionOpt.get();
            Double weight = toWeight(userAction.getActionType());
            Double newWeight = toWeight(ActionType.valueOf(avro.getActionType().name()));

            if (newWeight > weight) {
                userAction.setActionType(ActionType.valueOf(avro.getActionType().name()));
                userAction.setTimestamp(avro.getTimestamp());
                userActionRepository.save(userAction);
            }
        } else {
            UserAction userAction = UserAction.builder()
                    .userId(avro.getUserId())
                    .eventId(avro.getEventId())
                    .actionType(ActionType.valueOf(avro.getActionType().name()))
                    .timestamp(avro.getTimestamp())
                    .build();
            userActionRepository.save(userAction);
        }
    }

    private Double toWeight(ActionType actionType) {
        return switch (actionType) {
            case VIEW -> view;
            case REGISTER -> register;
            case LIKE -> like;
        };
    }
}