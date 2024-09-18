package com.example.board.service;

import com.example.board.entity.Post;
import com.example.board.exception.CsvProcessingException;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class CsvService {

    private final PostService postService;

    public void importPostsFromCsv(InputStreamReader inputStreamReader) {
        try (CSVParser csvParser = new CSVParser(inputStreamReader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            List<Post> posts = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                try {
                    Post post = Post.builder()
                            .userId(Long.parseLong(record.get(1)))
                            .title(record.get(2))
                            .body(record.get(3))
                            .build();
                    posts.add(post);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    throw new CsvProcessingException("Error parsing CSV record: " + record.toString(), e);
                }
            }

            if (!posts.isEmpty()) {
                postService.saveAll(posts);
            }
        } catch (Exception e) {
            throw new CsvProcessingException("Error reading CSV data.", e);
        }
    }
}
