package com.lauzon.stackOverflow.service;

import com.lauzon.stackOverflow.dto.response.QuestionVoteResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface QuestionUpVoteService {

    Map<String, Object> toggleUpvote(Long questionId);

    Page<QuestionVoteResponse> viewAllUpvoteOfAnswer(int page, int size, Long answerId);
}
