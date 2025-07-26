package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.exception.ConflictException;
import ru.practicum.error.exception.NotFoundException;
import ru.practicum.error.exception.ValidationException;
import ru.practicum.eventClient.event.InternalEventClient;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.eventClient.event.dto.EventState;
import ru.practicum.requestClient.dto.ParticipationRequestDto;
import ru.practicum.requestClient.dto.RequestUpdateDto;
import ru.practicum.requestClient.dto.RequestUpdateResultDto;
import ru.practicum.requests.mapper.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requestClient.dto.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.userClient.user.InternalUserClient;
import ru.practicum.userClient.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final InternalUserClient internalUserClient;
    private final InternalEventClient internalEventClient;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        checkUserExists(userId);

        return requestRepository.findByRequesterId(userId).stream()
                .map(requestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        UserDto user = checkUserExists(userId);
        EventFullDto event = checkEventExists(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может подать запрос на участие");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Событие ещё не опубликовано");
        }
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Запрос уже существует");
        }
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Лимит участников исчерпан");
        }

        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event.getId());
        request.setRequester(user.getId());
        request.setStatus(event.isRequestModeration() &&
                event.getParticipantLimit() > 0 ? RequestStatus.PENDING : RequestStatus.CONFIRMED);

        Request savedRequest = requestRepository.save(request);
        if (request.getStatus() == RequestStatus.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            internalEventClient.createEvent(event);
        }

        return requestMapper.toDto(savedRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkUserExists(userId);
        Request request = checkRequestExists(requestId);

        if (!request.getRequester().equals(userId)) {
            throw new ValidationException("Отменить запрос может только его создатель");
        }
        if (request.getStatus() == RequestStatus.CANCELED) {
            throw new ConflictException("Запрос уже отменён");
        }

        request.setStatus(RequestStatus.CANCELED);

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            EventFullDto event = checkEventExists(request.getEvent());
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            internalEventClient.createEvent(event);
        }

        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        checkUserExists(userId);
        EventFullDto event = checkEventExists(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Список запросов доступен только инициатору события");
        }

        return requestRepository.findByEventId(eventId).stream()
                .map(requestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public RequestUpdateResultDto updateEventRequests(Long userId, Long eventId, RequestUpdateDto updateDto) {
        checkUserExists(userId);
        EventFullDto event = checkEventExists(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Обновлять запросы может только инициатор события");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Событие ещё не опубликовано");
        }

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ValidationException("Модерация запроса на участие в этом событии не требуется");
        }

        List<Request> requests = requestRepository.findAllByIdIn(updateDto.getRequestIds());
        if (requests.size() != updateDto.getRequestIds().size()) {
            throw new NotFoundException("Часть заявок не найдена");
        }

        for (Request request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Нельзя изменить статус заявки, которая не находится в состоянии ожидания");
            }
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        int currentConfirmed = event.getConfirmedRequests();
        int participantLimit = event.getParticipantLimit();

        if ("CONFIRMED".equals(updateDto.getStatus()) && currentConfirmed >= participantLimit) {
            throw new ConflictException("Лимит участников исчерпан");
        }

        for (Request req : requests) {
            if ("CONFIRMED".equals(updateDto.getStatus())) {
                if (currentConfirmed < participantLimit) {
                    req.setStatus(RequestStatus.CONFIRMED);
                    currentConfirmed++;
                    confirmedRequests.add(requestMapper.toDto(req));
                } else {
                    req.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(requestMapper.toDto(req));
                }
            } else if ("REJECTED".equals(updateDto.getStatus())) {
                req.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(requestMapper.toDto(req));
            } else {
                throw new ValidationException("Неизвестный статус: " + updateDto.getStatus());
            }
        }

        requestRepository.saveAll(requests);
        event.setConfirmedRequests(currentConfirmed);
        internalEventClient.createEvent(event);

        return new RequestUpdateResultDto(confirmedRequests, rejectedRequests);
    }

    private UserDto checkUserExists(Long userId) {
        return internalUserClient.getUserById(userId);

    }

    private EventFullDto checkEventExists(Long eventId) {
        return internalEventClient.getEventById(eventId);
    }

    private Request checkRequestExists(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID=" + requestId + " не найден"));
    }
}