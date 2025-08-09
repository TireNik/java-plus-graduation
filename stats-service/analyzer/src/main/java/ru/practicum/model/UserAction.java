package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_actions")
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "action_type", nullable = false)
    @Enumerated(EnumType.STRING)
    ActionType actionType;

    private Instant timestamp;
}