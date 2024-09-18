package com.example.board.service;

import com.example.board.entity.Post;
import com.example.board.exception.PostNotFoundException;
import com.example.board.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private Post post1;
    private Post post2;

    @BeforeEach
    public void setUp() {
        // 테스트 데이터를 준비합니다.
        post1 = new Post(1L, 101L, "Title 1", "Body 1");
        post2 = new Post(2L, 102L, "Title 2", "Body 2");
    }

    @Test
    public void testGetAllPosts() {
        // Given
        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2));

        // When
        List<Post> result = postService.getAllPosts();

        // Then
        assertEquals(2, result.size());
        verify(postRepository, times(1)).findAll();
    }

    @Test
    public void testGetPostById_Found() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));

        // When
        Post result = postService.getPostById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetPostById_NotFound() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PostNotFoundException.class, () -> postService.getPostById(1L));
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreatePost() {
        // Given
        when(postRepository.save(post1)).thenReturn(post1);

        // When
        Post result = postService.createPost(post1);

        // Then
        assertNotNull(result);
        assertEquals("Title 1", result.getTitle());
        verify(postRepository, times(1)).save(post1);
    }

    @Test
    public void testUpdatePost() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        when(postRepository.save(any(Post.class))).thenReturn(post1);

        // When
        Post updatedPost = new Post(1L, 101L, "Updated Title", "Updated Body");
        Post result = postService.updatePost(1L, updatedPost);

        // Then
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(postRepository, times(1)).save(post1);
    }

    @Test
    public void testDeletePost() {
        // Given
        when(postRepository.existsById(1L)).thenReturn(true);

        // When
        postService.deletePost(1L);

        // Then
        verify(postRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testProcessAndSavePosts() {
        // Given
        List<Post> posts = Arrays.asList(post1, post2);
        when(postRepository.saveAll(anyList())).thenReturn(posts);

        // When
        List<Post> result = postService.processAndSvePosts(posts, "Title");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(postRepository, times(1)).saveAll(anyList());
    }
}
