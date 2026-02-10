package com.lauzon.stackOverflow.controller;

import com.lauzon.stackOverflow.dto.response.QuestionVoteResponse;
import com.lauzon.stackOverflow.service.impl.QuestionUpVoteServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users/questions/upvote")
@RequiredArgsConstructor
public class AnswerUpvoteController {

    private final QuestionUpVoteServiceImpl questionUpVoteService;

    @PostMapping("/{questionId}")
    public ResponseEntity<Map<String ,Object>> toggleAnswerUpVote(@PathVariable Long questionId) {
        Map<String, Object> response = questionUpVoteService.toggleUpvote(questionId);

        if (response.containsKey("message")) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all/{questionId}")
    public ResponseEntity<Page<QuestionVoteResponse>> viewAllUpvoteForAnswer(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size,
                                                                             @PathVariable Long questionId) {
        Page<QuestionVoteResponse> response = questionUpVoteService.viewAllUpvoteOfAnswer(page, size, questionId);
        return ResponseEntity.ok(response);
    }
}
