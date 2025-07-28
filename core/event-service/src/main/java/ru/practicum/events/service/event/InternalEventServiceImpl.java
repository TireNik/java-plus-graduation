package ru.practicum.events.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.error.exception.NotFoundException;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.mapper.LocationMapper;
import ru.practicum.events.model.event.Event;
import ru.practicum.events.model.event.Location;
import ru.practicum.events.repository.event.EventRepository;
import ru.practicum.events.repository.event.LocationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalEventServiceImpl implements InternalEventService{

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final LocationRepository locationRepository;

    @Override
    public EventFullDto getEventById(Long id) {
        log.info("Получение события с ID={}", id);
        return eventMapper.toEventFullDto(eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с ID=" + id + " не найдено")));
    }

    @Override
    public void createEvent(EventFullDto eventFullDto) {
        log.info("Создание события с ID={}", eventFullDto.getId());
        Location location = locationRepository.save(locationMapper.toEntity(eventFullDto.getLocation()));
        log.info("Место с ID={} успешно создано", location.getId());
        Event event = eventMapper.toEventFromFullDto(eventFullDto);
        event.setLocation(location);
        eventRepository.save(event);
        log.info("Событие с ID={} успешно создано", eventFullDto.getId());
    }
}
