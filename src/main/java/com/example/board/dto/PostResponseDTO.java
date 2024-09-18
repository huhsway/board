package com.example.board.dto;

import com.example.board.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class PostResponseDTO {

    private Long id;
    private Long userId;
    private String title;
    private String body;
    private List<CommentResponseDTO> comments;

    private PostResponseDTO postResponseDTO(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .body(post.getBody())
                .comments(post.getComments()
                        .stream().map(CommentResponseDTO::from)
                        .toList())
                .build();
    }

}
