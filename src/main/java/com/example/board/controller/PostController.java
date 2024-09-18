package com.example.board.controller;

import com.example.board.entity.Post;
import com.example.board.service.CsvService;
import com.example.board.service.ExternalApiService;
import com.example.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final ExternalApiService externalApiService;
    private final CsvService csvService;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        return ResponseEntity.ok(postService.updatePost(id, post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<String> saveAllPosts(@RequestBody List<Post> posts) {
        if (posts != null && !posts.isEmpty()) {
            postService.saveAll(posts);
            return ResponseEntity.status(HttpStatus.CREATED).body("All posts have been successfully saved to the database.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The post list is emtpy");
        }
    }

    @GetMapping("/external")
    public ResponseEntity<String> fetchAndSaveExternalPosts() {
        externalApiService.fetchAndSaveExternalPosts();
        return ResponseEntity.ok("External posts have been fetched and saved to the database.");
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCsvFile(@RequestParam("file") MultipartFile file) throws Exception {
        csvService.importPostsFromCsv(new InputStreamReader(file.getInputStream()));
        return ResponseEntity.status(HttpStatus.CREATED).body("CSV data has been successfully saved to the database.");
    }
}
