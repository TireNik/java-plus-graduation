package ru.practicum.events.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.error.exception.NotFoundException;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.repository.event.EventRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalEventServiceImpl implements InternalEventService{

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public EventFullDto getEventById(Long id) {
        log.info("Получение события с ID={}", id);
        return eventMapper.toEventFullDto(eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с ID=" + id + " не найдено")));
    }

    @Override
    public void createEvent(EventFullDto eventFullDto) {
        log.info("Создание события с ID={}", eventFullDto.getId());
        eventRepository.save(eventMapper.toEventFromFullDto(eventFullDto));
        log.info("Событие с ID={} успешно создано", eventFullDto.getId());
    }
}
