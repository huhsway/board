//package com.example.board.service;
//
//import com.example.board.entity.Post;
//import com.example.board.exception.ExternalApiException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//import reactor.core.publisher.Flux;
//
//import java.nio.charset.Charset;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//
//class ExternalApiServiceTest {
//
//    @Mock
//    private PostService postService;
//
//    @Mock
//    private WebClient.Builder webClientBuilder;
//
//    @Mock
//    private WebClient webClient;
//
//    @Mock
//    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
//
//    @Mock
//    private WebClient.ResponseSpec responseSpec;
//
//    @InjectMocks
//    private ExternalApiService externalApiService;
//
//    @Value("${external.api.base-url}")
//    private String baseUrl; // 테스트용 baseUrl 설정
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        given(webClientBuilder.baseUrl(baseUrl)).willReturn(webClientBuilder);
//        given(webClientBuilder.build()).willReturn(webClient);
//    }
//
//    @Test
//    void fetchAndSaveExternalPosts_success() {
//        // given
//        List<Post> mockPosts = List.of(new Post(1L, 1L, "Title 1", "Content 1"), new Post(2L, 2L, "Title 2", "Content 2"));
//        given(webClient.get()).willReturn(requestHeadersUriSpec);
//        given(requestHeadersUriSpec.uri("/posts")).willReturn(requestHeadersUriSpec);
//        given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
//        given(responseSpec.bodyToFlux(Post.class)).willReturn(Flux.fromIterable(mockPosts));
//
//        // when
//        externalApiService.fetchAndSaveExternalPosts();
//
//        // then
//        verify(postService, times(1)).saveAll(mockPosts);
//    }
//
//    @Test
//    void fetchAndSaveExternalPosts_externalApiException() {
//        // given
//        given(webClient.get()).willReturn(requestHeadersUriSpec);
//        given(requestHeadersUriSpec.uri("/posts")).willReturn(requestHeadersUriSpec);
//        given(requestHeadersUriSpec.retrieve()).willReturn(responseSpec);
//        given(responseSpec.bodyToFlux(Post.class)).willThrow(
//                WebClientResponseException.create(
//                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                        "Internal Server Error",
//                        null,
//                        null,
//                        Charset.defaultCharset()
//                )
//        );
//
//        // when & then
//        assertThrows(ExternalApiException.class, () -> externalApiService.fetchAndSaveExternalPosts());
//        verify(postService, never()).saveAll(any());
//    }
//}
