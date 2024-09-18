package com.example.board.service;

import com.example.board.dto.PostRequestDTO;
import com.example.board.entity.Post;
import com.example.board.exception.PostNotFoundException;
import com.example.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    @Cacheable(value = "posts")
    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Cacheable(value = "post", key = "#id")
    @Transactional(readOnly = true)
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    @CacheEvict(value = "posts", allEntries = true)
    @Transactional
    public Post createPost(PostRequestDTO postRequestDTO) {
        Post post = Post.builder()
                .userId(postRequestDTO.getUserId())
                .title(postRequestDTO.getTitle())
                .body(postRequestDTO.getBody())
                .build();
        return postRepository.save(post);
    }


    @CachePut(value = "post", key = "#id")
    @CacheEvict(value = "posts", allEntries = true)
    @Transactional
    public Post updatePost(Long id, PostRequestDTO postRequestDTO) {
        Post existingPost = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        existingPost.setUserId(postRequestDTO.getUserId());
        existingPost.setTitle(postRequestDTO.getTitle());
        existingPost.setBody(postRequestDTO.getBody());
        return postRepository.save(existingPost);
    }

    @CacheEvict(value = {"post", "posts"}, key = "#id", allEntries = true)
    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new PostNotFoundException(id);
        }
        postRepository.deleteById(id);
    }

    @CacheEvict(value = "posts", allEntries = true)
    @Transactional
    public void saveAll(List<Post> posts) {
        postRepository.saveAll(posts);
    }

    @Transactional
    public List<Post> processAndSavePosts(List<Post> posts, String keyword) {
        // 1. 필터링: 제목에 키워드가 포함된 게시물만 선택
        // 2. 매핑: 게시물의 내용을 대문자로 변환
        // 3. 그룹화 및 선택: userId로 그룹화하여 각 그룹에서 가장 최근의 게시물 선택
        // 4. 정렬 및 리스트 변환: 선택된 게시물을 id 순으로 정렬하여 리스트로 변환

        List<Post> processedPosts = posts.stream()
                // 1. 필터링: 제목에 키워드가 포함된 게시물만 선택
                .filter(post -> post.getTitle() != null && post.getTitle().contains(keyword))
                // 2. 매핑: 게시물의 내용을 대문자로 변환
                .map(post -> Post.builder()
                        .userId(post.getUserId())
                        .title(post.getTitle())
                        .body(post.getBody() != null ? post.getBody().toUpperCase() : null) // 내용을 대문자로 변환
                        .build())
                // 3. 그룹화 및 선택: userId로 그룹화하여 각 그룹에서 가장 최근의 게시물 선택
                .collect(Collectors.groupingBy(Post::getUserId,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Post::getId)),
                                optionalPost -> optionalPost.orElse(null))))
                // 4. 정렬 및 리스트 변환: 선택된 게시물을 id 순으로 정렬하여 리스트로 변환
                .values().stream()
                .filter(post -> post != null) // null 제거
                .sorted(Comparator.comparing(Post::getId))
                .toList();


        // 데이터베이스에 저장
        return postRepository.saveAll(processedPosts);

    }
}
