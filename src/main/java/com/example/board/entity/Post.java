package com.example.board.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Post {

    @Id
    private Long id;
    private Long userId;
    private String title;
    private String body;

    public Post(Long id, Long userId, String title, String body) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
    }

}
