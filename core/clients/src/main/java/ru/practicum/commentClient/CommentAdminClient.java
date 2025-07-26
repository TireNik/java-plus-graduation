package ru.practicum.commentClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.commentClient.dto.CommentDtoResponse;

@FeignClient(name = "comment-service", contextId = "CommentAdminClient", path = "/admin/comments")
public interface CommentAdminClient {

    @GetMapping("/{commentId}")
    CommentDtoResponse getComment(@PathVariable Long commentId);

    @DeleteMapping("/{commentId}")
    void deleteComment(@PathVariable Long commentId);
}