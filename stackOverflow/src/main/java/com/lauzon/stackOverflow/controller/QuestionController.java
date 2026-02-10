package com.lauzon.stackOverflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lauzon.stackOverflow.dto.request.QuestionRequest;
import com.lauzon.stackOverflow.dto.response.QuestionResponse;
import com.lauzon.stackOverflow.service.impl.QuestionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/questions")
public class QuestionController {

    private final QuestionServiceImpl questionService;

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            QuestionRequest request = objectMapper.readValue(requestJson, QuestionRequest.class);

            if (image != null && !image.isEmpty()) {
                request.setImageFile(image);
            }

            QuestionResponse savedQuestion = questionService.createQuestion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedQuestion);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @PatchMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @RequestPart("request") String requestString,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @PathVariable Long questionId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            QuestionRequest questionRequest = objectMapper.readValue(requestString, QuestionRequest.class);

            if (image != null && !image.isEmpty()) {
                questionRequest.setImageFile(image);
            }

            QuestionResponse updatedQuestion = questionService.updateQuestion(questionRequest, questionId);
            return ResponseEntity.ok(updatedQuestion);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/view/{questionId}")
    public ResponseEntity<Map<String, Object>> viewQuestion(@PathVariable Long questionId) {
         Map<String, Object> question = questionService.viewQuestion(questionId);
        return ResponseEntity.ok(question);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> viewAllQuestions(@RequestParam(defaultValue = "0") int page ,
                                                @RequestParam(defaultValue = "10") int size) {
       Map<String, Object> questions = questionService.viewAllQuestions(page, size);
       return ResponseEntity.ok(questions);
    }

    @GetMapping("/feeds")
    public ResponseEntity<Page<QuestionResponse>> feeds(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Page<QuestionResponse> feedsData = questionService.feeds(page, size);
        return ResponseEntity.ok(feedsData);
    }

    @GetMapping("/recents")
    public ResponseEntity<Page<QuestionResponse>> getRecentQuestions(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        Page<QuestionResponse> response = questionService.getRecentQuestions(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    public ResponseEntity<Page<QuestionResponse>> getPopularQuestions(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        Page<QuestionResponse> response = questionService.getPopularQuestions(page, size);
        return ResponseEntity.ok(response);
    }



}

