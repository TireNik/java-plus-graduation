package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.commentClient.dto.CommentDtoRequest;
import ru.practicum.commentClient.dto.CommentDtoResponse;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.model.Comment;
import ru.practicum.userClient.user.dto.UserDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "author", source = "user")
    @Mapping(target = "id", ignore = true)
    Comment commentDtotoComment(CommentDtoRequest commentDtoRequest, UserDto user, EventFullDto event);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "eventId", source = "event.id")
    CommentDtoResponse toResponseCommentDto(Comment comment);

    List<CommentDtoResponse> toResponseCommentDto(List<Comment> comments);
}
