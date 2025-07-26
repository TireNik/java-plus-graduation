package ru.practicum.commentClient;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.commentClient.dto.CommentDtoRequest;
import ru.practicum.commentClient.dto.CommentDtoResponse;

import java.util.List;

@FeignClient(name = "comment-service", contextId = "CommentPrivateClient", path = "/users/{userId}/comments")
public interface CommentPrivateClient {

    @GetMapping
    List<CommentDtoResponse> getComments(@PathVariable("userId") Long userId);

    @GetMapping("/{commentId}")
    CommentDtoResponse getComment(@PathVariable("userId") Long userId,
                                  @PathVariable("commentId") Long commentId);

    @PostMapping("/{eventId}")
    CommentDtoResponse createComment(@PathVariable("userId") Long userId,
                                     @PathVariable("eventId") Long eventId,
                                     @RequestBody @Valid CommentDtoRequest newComment);

    @PatchMapping("/{commentId}")
    CommentDtoResponse updateComment(@PathVariable("userId") Long userId,
                                     @PathVariable("commentId") Long commentId,
                                     @RequestBody @Valid CommentDtoRequest newComment);

    @DeleteMapping("/{commentId}")
    void deleteComment(@PathVariable("userId") Long userId,
                       @PathVariable("commentId") Long commentId);
}