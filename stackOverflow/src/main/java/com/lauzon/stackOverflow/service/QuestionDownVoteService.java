package com.lauzon.stackOverflow.service;

import com.lauzon.stackOverflow.dto.response.QuestionVoteResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface QuestionDownVoteService {

    Map<String, Object> toggleDownvoteForAnswer(Long questionId);

    Page<QuestionVoteResponse> viewAllDownvoteForAnswer(int page, int size, Long answerId);
}
