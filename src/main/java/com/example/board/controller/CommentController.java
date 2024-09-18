package com.example.board.controller;

import com.example.board.dto.CommentRequestDTO;
import com.example.board.dto.CommentResponseDTO;
import com.example.board.entity.Comment;
import com.example.board.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    // 특정 게시글에 댓글 추가
    @PostMapping("/posts/{postId}")
    public ResponseEntity<CommentResponseDTO> addCommentToPost(@PathVariable Long postId, @Valid @RequestBody CommentRequestDTO commentRequestDTO) {
        Comment createdComment = commentService.addCommentToPost(postId, commentRequestDTO);
        return new ResponseEntity<>(CommentResponseDTO.from(createdComment), HttpStatus.CREATED);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        List<CommentResponseDTO> responseDTOs = comments.stream()
                .map(CommentResponseDTO::from)
                .toList();
        return ResponseEntity.ok(responseDTOs);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

}
