package com.lauzon.stackOverflow.controller;

import com.lauzon.stackOverflow.dto.response.QuestionVoteResponse;
import com.lauzon.stackOverflow.service.impl.QuestionDownVoteServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/questions/downvote")
public class AnswerDownvoteController {

    private final QuestionDownVoteServiceImpl answerDownvoteService;

    @PostMapping("/{questionId}")
    public ResponseEntity<Map<String, Object>> toggleDownvoteForAnswer(@PathVariable Long questionId) {
        Map<String, Object> response = answerDownvoteService.toggleDownvoteForAnswer(questionId);

        if (response.containsKey("message")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all/{questionId}")
    public ResponseEntity<Page<QuestionVoteResponse>> viewAllDownvoteForAnswer(@RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size,
                                                                               @PathVariable Long questionId) {
        Page<QuestionVoteResponse> responses = answerDownvoteService.viewAllDownvoteForAnswer(page, size, questionId);
        return ResponseEntity.ok(responses);
    }
}
