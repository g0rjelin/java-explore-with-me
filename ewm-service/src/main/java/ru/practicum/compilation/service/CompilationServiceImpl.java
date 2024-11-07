package ru.practicum.compilation.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.stats.StatsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    final CompilationRepository compilationRepository;
    final EventRepository eventRepository;
    final StatsService statsService;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Compilation> compilations = Objects.isNull(pinned) ? compilationRepository.findAll(page).getContent() : compilationRepository.findAllByPinned(pinned, page);
        List<Event> events = compilations.stream().flatMap(c -> c.getEvents().stream()).distinct().collect(Collectors.toList());
        Map<Long, Long> views = statsService.getViewsForEvents(events);
        return CompilationMapper.toCompilationDto(compilations, views);
    }

    @Override
    public CompilationDto getCompilationByCompId(Long compilationId) {
        Compilation compilation = getCompilationById(compilationId);
        Map<Long, Long> views = statsService.getViewsForEvents(compilation.getEvents());
        return CompilationMapper.toCompilationDto(compilation, views);
    }

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Long> eventsIds = newCompilationDto.getEvents();
        List<Event> events = (!Objects.isNull(eventsIds) && !eventsIds.isEmpty()) ? eventRepository.findAllById(eventsIds) : new ArrayList<>();
        Map<Long, Long> views = statsService.getViewsForEvents(events);
        return CompilationMapper.toCompilationDto(compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events)), views);
    }

    @Override
    public CompilationDto update(Long compilationId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = getCompilationById(compilationId);
        List<Long> eventsIds = updateCompilationRequest.getEvents();
        if (!Objects.isNull(eventsIds) && !eventsIds.isEmpty()) {
            compilation.setEvents(eventRepository.findAllById(eventsIds));
        }
        compilation.setPinned(Objects.requireNonNullElse(updateCompilationRequest.getPinned(), compilation.isPinned()));
        compilation.setTitle(Objects.requireNonNullElse(updateCompilationRequest.getTitle(), compilation.getTitle()));
        Map<Long, Long> views = statsService.getViewsForEvents(compilation.getEvents());
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), views);
    }

    @Override
    public void delete(Long compilationId) {
        getCompilationById(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    private Compilation getCompilationById(Long compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException(compilationId, Compilation.class.toString()));
    }
}
