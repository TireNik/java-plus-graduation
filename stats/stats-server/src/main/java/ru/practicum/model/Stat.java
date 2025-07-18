package ru.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "stats")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "app", nullable = false)
    String app;

    @Column(name = "uri", nullable = false)
    String uri;

    @Column(name = "ip", nullable = false, length = 15)
    String ip;

    @Column(name = "date_stat", columnDefinition = "TIMESTAMP", nullable = false)
    LocalDateTime timestamp;
}
