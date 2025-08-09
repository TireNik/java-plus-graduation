package ru.practicum.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.grpc.stats.event.ActionTypeProto;
import ru.practicum.ewm.grpc.stats.event.UserActionProto;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.producer.KafkaProducerService;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActionHandlerImpl {
    final KafkaProducerService kafkaProducer;

    @Value("${kafka.topic}")
    String topic;

    public void handle(UserActionProto proto) {
        Instant timestamp = Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos());
        UserActionAvro avro = UserActionAvro.newBuilder()
                .setUserId(proto.getUserId())
                .setEventId(proto.getEventId())
                .setActionType(getActionTypeAvro(proto.getActionType()))
                .setTimestamp(timestamp)
                .build();
        kafkaProducer.send(avro, proto.getEventId(), timestamp, topic);
        log.info("Событие {} успешно отправлено в топик {}", avro, topic);
    }

    private ActionTypeAvro getActionTypeAvro(ActionTypeProto actionType) {
        return switch (actionType) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            default -> throw new IllegalArgumentException("Неизвестный тип действия = " + actionType);
        };
    }
}
