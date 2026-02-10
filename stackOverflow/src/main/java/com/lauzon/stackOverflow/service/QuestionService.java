package com.lauzon.stackOverflow.service;


import com.lauzon.stackOverflow.dto.request.QuestionRequest;
import com.lauzon.stackOverflow.dto.response.QuestionResponse;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.Map;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest questionRequest) throws IOException;

    QuestionResponse updateQuestion(QuestionRequest questionRequest, Long questionId) throws IOException;

    void deleteQuestion(Long questionId);

    Map<String, Object> viewQuestion(Long questionId);

    Map<String, Object> viewAllQuestions(int page, int size);

    Page<QuestionResponse> feeds(int page, int size);

    Page<QuestionResponse> getRecentQuestions(int page, int size);

    Page<QuestionResponse> getPopularQuestions(int page, int size);
}
