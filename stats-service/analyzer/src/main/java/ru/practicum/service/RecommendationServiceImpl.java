package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.grpc.stats.event.InteractionsCountRequestProto;
import ru.practicum.ewm.grpc.stats.event.RecommendedEventProto;
import ru.practicum.ewm.grpc.stats.event.SimilarEventsRequestProto;
import ru.practicum.ewm.grpc.stats.event.UserPredictionsRequestProto;
import ru.practicum.model.ActionType;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.UserAction;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserActionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    @Value("${application.action-weight.view}")
    private double view;
    @Value("${application.action-weight.register}")
    private double register;
    @Value("${application.action-weight.like}")
    private double like;

    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        Long userId = request.getUserId();
        int limit = request.getMaxResults();
        PageRequest pageRequest = PageRequest.of(0, limit,
                Sort.by(Sort.Direction.DESC, "timestamp"));

        Set<Long> recentlyViewedEventIds = userActionRepository.findAllByUserId(userId, pageRequest).stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        if (recentlyViewedEventIds.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> candidateEventIds = findCandidateRecommendations(userId, recentlyViewedEventIds, limit);

        return generateRecommendations(candidateEventIds, userId, limit);
    }

    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        Long eventId = request.getEventId();
        Long userId = request.getUserId();
        PageRequest pageRequest = PageRequest.of(0, request.getMaxResults(),
                Sort.by(Sort.Direction.DESC, "score"));

        List<EventSimilarity> similaritiesA = eventSimilarityRepository.findAllByEventA(eventId, pageRequest);
        List<EventSimilarity> similaritiesB = eventSimilarityRepository.findAllByEventB(eventId, pageRequest);

        List<RecommendedEventProto> recommendations = new ArrayList<>();

        addFilteredRecommendations(recommendations, similaritiesA,true, userId);
        addFilteredRecommendations(recommendations, similaritiesB,false, userId);

        recommendations.sort(Comparator.comparing(RecommendedEventProto::getScore).reversed());

        return recommendations.size() > request.getMaxResults()
                ? recommendations.subList(0, request.getMaxResults())
                : recommendations;
    }

    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        Set<Long> eventIds = new HashSet<>(request.getEventIdList());

        Map<Long, Double> eventScores = new HashMap<>();

        userActionRepository.findAllByEventIdIn(eventIds).forEach(action -> {
            long eventId = action.getEventId();
            double weight = toWeight(action.getActionType());
            eventScores.merge(eventId, weight, Double::sum);
        });

        return eventScores.entrySet().stream()
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build())
                .toList();
    }

    private Set<Long> findCandidateRecommendations(Long userId, Set<Long> viewedEventIds, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score"));

        List<EventSimilarity> similaritiesA = eventSimilarityRepository.findAllByEventAIn(viewedEventIds, pageRequest);
        List<EventSimilarity> similaritiesB = eventSimilarityRepository.findAllByEventBIn(viewedEventIds, pageRequest);

        Set<Long> recommendations = new HashSet<>();

        addNewEventsFromSimilarities(similaritiesA, true, userId, recommendations);
        addNewEventsFromSimilarities(similaritiesB, false, userId, recommendations);

        return recommendations;
    }

    private void addNewEventsFromSimilarities(List<EventSimilarity> similarities,
                                              boolean isEventB,
                                              Long userId,
                                              Set<Long> result) {
        for (EventSimilarity es : similarities) {
            Long candidateId = isEventB ? es.getEventB() : es.getEventA();
            if (!userActionRepository.existsByEventIdAndUserId(candidateId, userId)) {
                result.add(candidateId);
            }
        }
    }

    private List<RecommendedEventProto> generateRecommendations(Set<Long> candidateEventIds,
                                                                Long userId,
                                                                int limit) {
        // Рассчитываем score для каждого кандидата
        Map<Long, Double> eventScores = candidateEventIds.stream()
                .collect(Collectors.toMap(eventId -> eventId,
                        eventId -> calculateRecommendationScore(eventId, userId, limit)));

        return eventScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> buildRecommendation(entry.getKey(), entry.getValue()))
                .toList();
    }

    private Double calculateRecommendationScore(Long eventId, Long userId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score"));
        List<EventSimilarity> similaritiesA = eventSimilarityRepository.findAllByEventA(eventId, pageRequest);
        List<EventSimilarity> similaritiesB = eventSimilarityRepository.findAllByEventB(eventId, pageRequest);

        Map<Long, Double> similarityScores = new HashMap<>();
        collectViewedSimilarities(similaritiesA, true, userId, similarityScores);
        collectViewedSimilarities(similaritiesB, false, userId, similarityScores);

        Map<Long, Double> userRatings = userActionRepository.findAllByEventIdInAndUserId(
                        similarityScores.keySet(), userId).stream()
                .collect(Collectors.toMap(UserAction::getEventId,
                        userAction -> toWeight(userAction.getActionType())));

        double sumWeightedRatings = 0.0;
        double sumSimilarityScores = 0.0;

        for (Map.Entry<Long, Double> entry : similarityScores.entrySet()) {
            Long viewedEventId = entry.getKey();
            Double userRating = userRatings.get(viewedEventId);
            if (userRating != null) {
                sumWeightedRatings += userRating * entry.getValue();
                sumSimilarityScores += entry.getValue();
            }
        }

        return sumSimilarityScores > 0 ? sumWeightedRatings / sumSimilarityScores : 0.0;
    }

    private void collectViewedSimilarities(List<EventSimilarity> similarities,
                                           boolean isEventB,
                                           Long userId,
                                           Map<Long, Double> result) {
        for (EventSimilarity es : similarities) {
            Long relatedEventId = isEventB ? es.getEventB() : es.getEventA();
            if (userActionRepository.existsByEventIdAndUserId(relatedEventId, userId)) {
                result.put(relatedEventId, es.getScore());
            }
        }
    }

    private RecommendedEventProto buildRecommendation(Long eventId, Double score) {
        return RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore(score)
                .build();
    }

    private void addFilteredRecommendations(List<RecommendedEventProto> recommendations,
                                            List<EventSimilarity> similarities,
                                            boolean isEventB,
                                            Long userId) {
        for (EventSimilarity es : similarities) {
            Long candidateEventId = isEventB ? es.getEventB() : es.getEventA();

            if (!userActionRepository.existsByEventIdAndUserId(candidateEventId, userId)) {
                recommendations.add(RecommendedEventProto.newBuilder()
                        .setEventId(candidateEventId)
                        .setScore(es.getScore())
                        .build());
            }
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
