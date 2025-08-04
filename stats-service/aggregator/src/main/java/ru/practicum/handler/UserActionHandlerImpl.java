package ru.practicum.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserActionHandlerImpl implements UserActionHandler {

    // Действия пользователей с мероприятиями Map<Event, Map<User, Weight>>
    private final Map<Long, Map<Long, Double>> eventActions = new HashMap<>();
    // Сумма весов действий пользователей с мероприятием
    private final Map<Long, Double> eventWeights = new HashMap<>();
    // Сумма минимальных весов для каждой пары мероприятий Map<Event, Map<Event, S_min>>
    Map<Long, Map<Long, Double>> minWeightsSum = new HashMap<>();

    @Value("${application.action-weight.view}")
    private double view;
    @Value("${application.action-weight.register}")
    private double register;
    @Value("${application.action-weight.like}")
    private double like;


    @Override
    public List<EventSimilarityAvro> calculateSimilarity(UserActionAvro userActionAvro) {

        List<EventSimilarityAvro> result = new ArrayList<>();
        Long userId = userActionAvro.getUserId();
        Long eventIdA = userActionAvro.getEventId();
        Double newWeight = getActionWeight(userActionAvro.getActionType());

        Map<Long, Double> eventUserWeights = eventActions.computeIfAbsent(eventIdA, k -> new HashMap<>());
        double oldWeight = eventUserWeights.getOrDefault(userId, 0.0);

        if (newWeight > oldWeight) {
            double deltaWeight = newWeight - oldWeight;
            eventUserWeights.put(userId, newWeight);
            eventWeights.merge(eventIdA, deltaWeight, Double::sum);

            for(Map.Entry<Long, Map<Long, Double>> entry : eventActions.entrySet()) {
                Long eventIdB = entry.getKey();
                if (eventIdA.equals(eventIdB)) continue;

                Map<Long, Double> eventUserWeights2 = entry.getValue();
                Double otherWeight = eventUserWeights2.get(userId);
                if (otherWeight == null) continue;

                double minWeight = Math.min(newWeight, otherWeight);
                long a = Math.min(eventIdA, eventIdB);
                long b = Math.max(eventIdA, eventIdB);

                minWeightsSum.computeIfAbsent(a, k -> new HashMap<>())
                        .merge(b, minWeight - Math.min(oldWeight, otherWeight), Double::sum);


                double sMin = minWeightsSum.get(a).get(b) /
                        (Math.sqrt(eventWeights.get(a)) * Math.sqrt(eventWeights.get(b)));

                if (sMin > 0.0) {
                    result.add(
                            EventSimilarityAvro.newBuilder()
                                    .setEventA(eventIdA)
                                    .setEventB(eventIdB)
                                    .setScore(sMin)
                                    .setTimestamp(Instant.now())
                                    .build());
                }

                return result;
            }
        }

        return List.of();
    }

    private Double getActionWeight(ActionTypeAvro action) {
        return switch (action) {
            case VIEW -> view;
            case REGISTER -> register;
            case LIKE -> like;
        };
    }
}
