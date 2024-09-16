package com.example.board.controller;

import com.example.board.entity.Post;
import com.example.board.service.CsvService;
import com.example.board.service.ExternalApiService;
import com.example.board.service.PostCrudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostCrudService postCrudService;

    @MockBean
    private ExternalApiService externalApiService;

    @MockBean
    private CsvService csvService;

    private Post post1;
    private Post post2;

    @BeforeEach
    public void setUp() {
        post1 = new Post(1L, 101L, "Title 1", "Body 1");
        post2 = new Post(2L, 102L, "Title 2", "Body 2");
    }

    @Test
    public void testGetAllPosts() throws Exception {
        List<Post> mockPosts = Arrays.asList(post1, post2);

        when(postCrudService.getAllPosts()).thenReturn(mockPosts);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(mockPosts.size()));

        verify(postCrudService, times(1)).getAllPosts();
    }

    @Test
    public void testGetPostById() throws Exception {
        when(postCrudService.getPostById(1L)).thenReturn(post1);

        mockMvc.perform(get("/api/posts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title 1"));

        verify(postCrudService, times(1)).getPostById(1L);
    }

    @Test
    public void testCreatePost() throws Exception {
        when(postCrudService.createPost(any(Post.class))).thenReturn(post1);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 101, \"title\": \"Title 1\", \"body\": \"Body 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Title 1"));

        verify(postCrudService, times(1)).createPost(any(Post.class));
    }

    @Test
    public void testUpdatePost() throws Exception {
        when(postCrudService.updatePost(anyLong(), any(Post.class))).thenReturn(post1);

        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 101, \"title\": \"Updated Title\", \"body\": \"Updated Body\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title 1"));

        verify(postCrudService, times(1)).updatePost(anyLong(), any(Post.class));
    }

    @Test
    public void testDeletePost() throws Exception {
        doNothing().when(postCrudService).deletePost(1L);

        mockMvc.perform(delete("/api/posts/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(postCrudService, times(1)).deletePost(1L);
    }

    @Test
    public void testSaveAllPosts() throws Exception {
        doNothing().when(postCrudService).saveAll(anyList());

        mockMvc.perform(post("/api/posts/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"userId\": 101, \"title\": \"Title 1\", \"body\": \"Body 1\"}]"))
                .andExpect(status().isCreated())
                .andExpect(content().string("All posts have been successfully saved to the database."));

        verify(postCrudService, times(1)).saveAll(anyList());
    }

    @Test
    public void testFetchAndSaveExternalPosts() throws Exception {
        doNothing().when(externalApiService).fetchAndSaveExternalPosts();

        mockMvc.perform(get("/api/posts/external"))
                .andExpect(status().isOk())
                .andExpect(content().string("External posts have been fetched and saved to the database."));

        verify(externalApiService, times(1)).fetchAndSaveExternalPosts();
    }

    @Test
    public void testUploadCsvFile() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.csv", "text/csv", "userId,id,title,body\n1,101,Title 1,Body 1".getBytes());

        doNothing().when(csvService).importPostsFromCsv(any(InputStreamReader.class));

        mockMvc.perform(multipart("/api/posts/upload-csv")
                        .file(mockFile))
                .andExpect(status().isCreated())
                .andExpect(content().string("CSV data has been successfully saved to the database."));

        verify(csvService, times(1)).importPostsFromCsv(any(InputStreamReader.class));
    }
}
