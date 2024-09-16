package com.example.board.service;

import com.example.board.entity.Post;
import com.example.board.exception.CsvProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class CsvServiceTest {

    @Mock
    private PostCrudService postCrudService;

    @InjectMocks
    private CsvService csvService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testImportPostsFromCsv_Success() throws Exception {
        // Given
        String csvData = "id,userId,title,body\n" +
                "1,101,Title 1,Body 1\n" +
                "2,102,Title 2,Body 2\n";

        // Convert string to InputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));

        // When
        csvService.importPostsFromCsv(new InputStreamReader(inputStream));

        // Then
        verify(postCrudService, times(1)).saveAll(anyList());
    }

    @Test
    public void testImportPostsFromCsv_EmptyFile() throws Exception {
        // Given
        String csvData = "id,userId,title,body\n"; // CSV with header only, no data

        // Convert string to InputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));

        // When
        csvService.importPostsFromCsv(new InputStreamReader(inputStream));

        // Then
        verify(postCrudService, never()).saveAll(anyList()); // saveAll should not be called
    }

    @Test
    public void testImportPostsFromCsv_MalformedData() {
        // Given
        String csvData = "id,userId,title,body\n" +
                "1,abc,Title 1,Body 1\n"; // Malformed data: userId is not a number

        // Convert string to InputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));

        // When & Then
        assertThrows(CsvProcessingException.class, () ->
                csvService.importPostsFromCsv(new InputStreamReader(inputStream))
        );

        verify(postCrudService, never()).saveAll(anyList());
    }

    @Test
    public void testImportPostsFromCsv_PartialMalformedData() {
        // Given
        String csvData = "id,userId,title,body\n" +
                "1,101,Title 1,Body 1\n" +
                "2,notanumber,Title 2,Body 2\n"; // Second row has malformed userId

        // Convert string to InputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));

        // When
        assertThrows(CsvProcessingException.class, () ->
                csvService.importPostsFromCsv(new InputStreamReader(inputStream))
        );

        // Since we throw exception on malformed data, `saveAll` should not be called at all
        verify(postCrudService, never()).saveAll(anyList());
    }
}
