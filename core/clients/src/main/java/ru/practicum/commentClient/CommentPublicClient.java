package ru.practicum.commentClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.commentClient.dto.CommentDtoResponse;

import java.util.List;

@FeignClient(name = "comment-service", path = "/comments")
public interface CommentPublicClient {

    @GetMapping("/{eventId}")
    List<CommentDtoResponse> getComments(@PathVariable("eventId") Long eventId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size);
}