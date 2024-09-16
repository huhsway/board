package com.example.board.service;

import com.example.board.entity.Post;
import com.example.board.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final PostCrudService postCrudService;
    private final WebClient.Builder webClientBuilder; // WebClient.Builder 주입

    @Value("${external.api.base-url}")
    private String baseUrl;

    public void fetchAndSaveExternalPosts() {
        try {
            WebClient client = webClientBuilder.baseUrl(baseUrl).build();
            List<Post> posts = client.get()
                    .uri("/posts")
                    .retrieve()
                    .bodyToFlux(Post.class)
                    .collectList()
                    .block();

            if (posts != null && !posts.isEmpty()) {
                postCrudService.saveAll(posts);
            }
        } catch (Exception e) {
            throw new ExternalApiException("Failed to fetch and save external posts.", e);
        }
    }

}
