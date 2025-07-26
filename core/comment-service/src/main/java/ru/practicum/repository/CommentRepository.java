package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> getCommentsByAuthorId(Long userId);

    @Query("SELECT c FROM Comment c WHERE c.event = ?1")
    List<Comment> findAllCommentsForEvent(Long eventId, Pageable pageable);

}