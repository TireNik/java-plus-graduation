package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.commentClient.dto.CommentDtoRequest;
import ru.practicum.commentClient.dto.CommentDtoResponse;
import ru.practicum.eventClient.event.InternalEventClient;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.eventClient.event.dto.EventState;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;
import ru.practicum.error.exception.ConflictException;
import ru.practicum.error.exception.ResourceNotFoundException;
import ru.practicum.userClient.user.InternalUserClient;
import ru.practicum.userClient.user.dto.UserDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceManager implements CommentService {
    private final CommentRepository commentRepository;
    private final InternalUserClient internalUserClient;
    private final InternalEventClient internalEventClient;
    private final CommentMapper commentMapper;

    @Override
    public CommentDtoResponse getCommentAdmin(Long commentId) {
        CommentDtoResponse responseCommentDto = commentMapper.toResponseCommentDto(
                commentRepository.findById(commentId)
                        .orElseThrow(() -> new ResourceNotFoundException(Comment.class, commentId)));

        log.info("Получение администратором комментария c id {}.", commentId);
        return responseCommentDto;
    }

    @Override
    @Transactional
    public void deleteCommentAdmin(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException(Comment.class, commentId);
        }
        commentRepository.deleteById(commentId);
        log.info("Удаление администратором комментария c id {}.", commentId);
    }

    @Override
    public List<CommentDtoResponse> getCommentsOfEventPublic(Long eventId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());

        List<Comment> comments = commentRepository.findAllCommentsForEvent(eventId, pageable);
        List<CommentDtoResponse> responseCommentsDTO = commentMapper.toResponseCommentDto(comments);

        log.info("Получение всех комментариев для события c id {} по параметрам: from: {}, size: {}.",
                eventId, from, size);
        return responseCommentsDTO;
    }

    @Override
    public List<CommentDtoResponse> getAllCommentsByUserIdPrivate(Long userId) {
        List<CommentDtoResponse> responseCommentsDTO = commentMapper.toResponseCommentDto(
                commentRepository.getCommentsByAuthorId(userId));

        log.info("Получение комментариев вользователя с id {}.", userId);
        return responseCommentsDTO;
    }

    @Override
    public CommentDtoResponse getCommentByIdByUserIdPrivate(Long userId, Long commentId) {

        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(Comment.class, commentId));
        if (!foundComment.getId().equals(userId)) {
            throw new ConflictException("Данный пользователь не является автором получаемого комментария.");
        }
        CommentDtoResponse responseCommentDto = commentMapper.toResponseCommentDto(foundComment);

        log.info("Получение комментария c id {} пользователя c id {}.", commentId, userId);
        return responseCommentDto;
    }

    @Override
    @Transactional
    public CommentDtoResponse createCommentPrivate(CommentDtoRequest newComment, Long userId, Long eventId) {
        EventFullDto event = internalEventClient.getEventById(eventId);
        UserDto user = checkUserExists(userId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Комментируемое событие не опубликовано.");
        }
        Comment comment = commentRepository.save(commentMapper.commentDtotoComment(newComment, user, event));
        CommentDtoResponse responseCommentDto = commentMapper.toResponseCommentDto(comment);

        log.info("Создание комментария {} пользователем с  id {} к событию c id {}.", newComment, userId, eventId);
        return responseCommentDto;
    }

    @Override
    @Transactional
    public CommentDtoResponse updateCommentPrivate(CommentDtoRequest newComment, Long userId, Long commentId) {
        if (checkUserExists(userId) == null) {
            throw new ResourceNotFoundException(UserDto.class, userId);
        }
        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(Comment.class, commentId));
        if (!foundComment.getId().equals(userId)) {
            throw new ConflictException("Данный пользователь не является автором обновляемого комментария.");
        }
        foundComment.setMessage(newComment.getMessage());
        CommentDtoResponse commentDtoResponse = commentMapper.toResponseCommentDto(commentRepository.save(foundComment));

        log.info("Обновление комментария c id {} пользователем c id {} на новый комментарий {}.", commentId,
                userId, newComment);
        return commentDtoResponse;
    }

    @Override
    @Transactional
    public void deleteByIdByUser(Long userId, Long commentId) {
        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(Comment.class, commentId));
        if (!foundComment.getId().equals(userId)) {
            throw new ConflictException("Данный пользователь не является автором удаляемого комментария.");
        }
        commentRepository.deleteById(commentId);
        log.info("Удаление комментария c id {} пользователем c id {}.", commentId, userId);
    }

    private UserDto checkUserExists(Long id) {
        return internalUserClient.getUserById(id);
    }
}