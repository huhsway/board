package com.example.board.dto;

import com.example.board.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentResponseDTO {

    private Long id;
    private String content;
    private Long postId;

    public static CommentResponseDTO from(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .build();
    }

}
