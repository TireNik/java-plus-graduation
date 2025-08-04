package ru.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.grpc.stats.event.ActionTypeProto;
import ru.practicum.ewm.grpc.stats.event.UserActionProto;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.producer.KafkaProducer;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserActionHandlerImpl implements UserActionHandler {
    private final KafkaProducer kafkaProducer;

    @Value("${collection.topic.user-action}")
    private String topic;

    @Override
    public void handleAction(UserActionProto userActionProto) {
        var contract = UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setActionType(getActionType(userActionProto.getActionType()))
                .setTimestamp(mapTimestampToInstant(userActionProto))
                .build();
        log.info("Отправка события в Kafka: {}", contract);
        kafkaProducer.send(contract, mapTimestampToInstant(userActionProto), userActionProto.getEventId(), topic);

    }

    private Instant mapTimestampToInstant(UserActionProto userActionProto) {
        return Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds(), userActionProto.getTimestamp().getNanos());
    }

    private ActionTypeAvro getActionType(ActionTypeProto actionTypeProto) {
        return switch (actionTypeProto) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            case UNRECOGNIZED -> null;
        };
    }
}
