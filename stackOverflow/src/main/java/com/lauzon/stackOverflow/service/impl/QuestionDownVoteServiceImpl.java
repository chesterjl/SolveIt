package com.lauzon.stackOverflow.service.impl;

import com.lauzon.stackOverflow.dto.response.QuestionVoteResponse;
import com.lauzon.stackOverflow.entity.QuestionDownVoteEntity;
import com.lauzon.stackOverflow.entity.QuestionEntity;
import com.lauzon.stackOverflow.entity.UserEntity;
import com.lauzon.stackOverflow.exception.ResourceNotFoundException;
import com.lauzon.stackOverflow.repository.QuestionDownVoteRepository;
import com.lauzon.stackOverflow.repository.QuestionRepository;
import com.lauzon.stackOverflow.service.QuestionDownVoteService;
import com.lauzon.stackOverflow.util.UtilMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionDownVoteServiceImpl implements QuestionDownVoteService {

    private final QuestionDownVoteRepository questionDownVoteRepository;
    private final QuestionRepository questionRepository;
    private final UtilMethod utilMethod;

    @Override
    public Map<String, Object> toggleDownvoteForAnswer(Long questionId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        UserEntity user = utilMethod.getCurrentUser();

        Optional<QuestionDownVoteEntity> existingDownvote = questionDownVoteRepository.findByQuestionIdAndUserId(questionId, user.getId());

        if (existingDownvote.isPresent()) {
            questionDownVoteRepository.delete(existingDownvote.get());
            return Map.of("message", "Downvote deleted");
        }

        QuestionDownVoteEntity unliked = convertToEntity(user, question);
        unliked = questionDownVoteRepository.save(unliked);
        return Map.of("response", convertToResponse(unliked));
    }

    @Override
    public Page<QuestionVoteResponse> viewAllDownvoteForAnswer(int page, int size, Long questionId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<QuestionDownVoteEntity> fetchAllDownvoteForAnswer = questionDownVoteRepository.findAllByQuestionId(questionId, pageable);
        return fetchAllDownvoteForAnswer.map(this::convertToResponse);
    }


    private QuestionDownVoteEntity convertToEntity(UserEntity user, QuestionEntity question) {
        return QuestionDownVoteEntity.builder()
                .user(user)
                .question(question)
                .build();
    }

    private QuestionVoteResponse convertToResponse(QuestionDownVoteEntity questionDownVoteEntity) {
        return QuestionVoteResponse.builder()
                .id(questionDownVoteEntity.getId())
                .questionId(questionDownVoteEntity.getQuestion().getId())
                .userId(questionDownVoteEntity.getUser().getId())
                .name(questionDownVoteEntity.getUser().getFirstName() + " " + questionDownVoteEntity.getUser().getLastName())
                .createdAt(questionDownVoteEntity.getCreatedAt())
                .build();
    }

}
