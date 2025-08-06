package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.grpc.stats.event.InteractionsCountRequestProto;
import ru.practicum.ewm.grpc.stats.event.RecommendedEventProto;
import ru.practicum.ewm.grpc.stats.event.SimilarEventsRequestProto;
import ru.practicum.ewm.grpc.stats.event.UserPredictionsRequestProto;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.UserAction;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserActionRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    @Override
    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        Long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        List<UserAction> userActions = userActionRepository.findAllByUserId(userId,
                PageRequest.of(0, maxResults, Sort.by(Sort.Direction.DESC, "timestamp")));

        if (userActions.isEmpty()) {
            return List.of();
        }

        Set<Long> eventIds = userActions.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        List<EventSimilarity> similarEvents = eventSimilarityRepository.findAllByEventAInOrEventBIn(
                eventIds, eventIds,
                PageRequest.of(0, maxResults * 2, Sort.by(Sort.Direction.DESC, "score")
                ));

        Set<Long> allUserEventIds = userActionRepository.findAllEventIdsByUserId(userId);

        return processSimilarEvents(similarEvents, eventIds, allUserEventIds, userId, maxResults);

    }

    @Override
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        Long eventId = request.getEventId();
        Long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        List<EventSimilarity> eventSimilarities = eventSimilarityRepository.findAllByEventAOrEventB(
                eventId, eventId,
                PageRequest.of(0, maxResults * 2, Sort.by(Sort.Direction.DESC, "score")));

        if (eventSimilarities.isEmpty()) {
            return List.of();
        }

        Set<Long> userViewedEvents = userActionRepository.findAllEventIdsByUserId(userId);
        Set<Long> sourceEventIds = Set.of(eventId);

        return processSimilarEvents(eventSimilarities, sourceEventIds, userViewedEvents, userId, maxResults);
    }

    @Override
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        return request.getEventIdList().stream()
                .map(eId -> {
                    Float score = userActionRepository.getSumWeightByEventId(eId);
                    return RecommendedEventProto.newBuilder()
                            .setEventId(eId)
                            .setScore(score != null ? score : 0f)
                            .build();
                })
                .sorted(Comparator.comparing(RecommendedEventProto::getScore).reversed())
                .collect(Collectors.toList());
    }

    private List<RecommendedEventProto> processSimilarEvents(List<EventSimilarity> similarEvents,
                                                             Set<Long> eventIds,
                                                             Set<Long> allUserEventIds,
                                                             Long userId,
                                                             int maxResults) {
        Set<Long> newEventIds = similarEvents.stream()
                .map(es -> eventIds.contains(es.getEventA()) ? es.getEventB() : es.getEventA())
                .filter(eventId -> !allUserEventIds.contains(eventId))
                .limit(maxResults * 2L)
                .collect(Collectors.toSet());

        return newEventIds.stream()
                .map(eventId -> RecommendedEventProto.newBuilder()
                        .setEventId(eventId)
                        .setScore(calculateScore(eventId, userId, allUserEventIds, maxResults))
                        .build())
                .sorted(Comparator.comparing(RecommendedEventProto::getScore).reversed())
                .limit(maxResults)
                .collect(Collectors.toList());

    }

    private float calculateScore(Long eventId, Long userId, Set<Long> allUserEventIds, int maxResults) {
        List<EventSimilarity> similarEvents = eventSimilarityRepository.findAllByEventAOrEventB(
                eventId, eventId,
                PageRequest.of(0, maxResults * 2, Sort.by(Sort.Direction.DESC, "score")
                ));


        Map<Long, Double> scores = similarEvents.stream()
                .filter(es -> {
                    Long other = es.getEventA().equals(eventId) ? es.getEventB() : es.getEventA();
                    return allUserEventIds.contains(other);
                })
                .collect(Collectors.toMap(
                        es -> es.getEventA().equals(eventId) ? es.getEventB() : es.getEventA(),
                        EventSimilarity::getScore));

        if (scores.isEmpty()) {
            return 0f;
        }

        Map<Long, Float> actions = userActionRepository.findAllByEventIdInAndUserId(scores.keySet(), userId).stream()
                .collect(Collectors.toMap(UserAction::getEventId, UserAction::getMark));

        double weight = scores.entrySet().stream()
                .mapToDouble(e -> actions.getOrDefault(e.getKey(), 0.0f) * e.getValue())
                .sum();

        double sum = scores.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        return sum > 0 ? (float) (weight / sum) : 0f;
    }

}
