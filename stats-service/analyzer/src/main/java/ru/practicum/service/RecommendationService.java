package ru.practicum.service;

import ru.practicum.ewm.grpc.stats.event.RecommendedEventProto;
import ru.practicum.ewm.grpc.stats.event.UserPredictionsRequestProto;

import java.util.List;

public interface RecommendationService {
    List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request);
}
