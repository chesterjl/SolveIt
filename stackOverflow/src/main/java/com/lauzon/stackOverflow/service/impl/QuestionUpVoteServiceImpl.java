package com.lauzon.stackOverflow.service.impl;

import com.lauzon.stackOverflow.dto.response.QuestionVoteResponse;
import com.lauzon.stackOverflow.entity.QuestionUpVoteEntity;
import com.lauzon.stackOverflow.entity.QuestionEntity;
import com.lauzon.stackOverflow.entity.UserEntity;
import com.lauzon.stackOverflow.exception.ResourceNotFoundException;
import com.lauzon.stackOverflow.repository.QuestionUpVoteRepository;
import com.lauzon.stackOverflow.repository.QuestionRepository;
import com.lauzon.stackOverflow.service.QuestionUpVoteService;
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
public class QuestionUpVoteServiceImpl implements QuestionUpVoteService {

    private final UtilMethod utilMethod;
    private final QuestionUpVoteRepository questionUpVoteRepository;
    private final QuestionRepository questionRepository;

    @Override
    public Map<String, Object> toggleUpvote(Long questionId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        UserEntity user = utilMethod.getCurrentUser();

        Optional<QuestionUpVoteEntity> existingUpvote = questionUpVoteRepository.findByQuestionIdAndUserId(questionId, user.getId());

        if (existingUpvote.isPresent()) {
            questionUpVoteRepository.delete(existingUpvote.get());
            return Map.of("message", "Upvote deleted");
        }

        QuestionUpVoteEntity liked = convertToEntity(user, question);
        liked = questionUpVoteRepository.save(liked);
        return Map.of("response", convertToResponse(liked));
    }

    @Override
    public Page<QuestionVoteResponse> viewAllUpvoteOfAnswer(int page, int size, Long questionId) {
        QuestionEntity existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QuestionUpVoteEntity> allUpvote = questionUpVoteRepository.findAllByQuestionId(questionId, pageable);
        System.out.println("Votes found in DB: " + allUpvote.getTotalElements());
        return allUpvote.map(this::convertToResponse);
    }

    public QuestionUpVoteEntity convertToEntity(UserEntity user, QuestionEntity question) {
        return QuestionUpVoteEntity.builder()
                .user(user)
                .question(question)
                .build();
    }

    public QuestionVoteResponse convertToResponse(QuestionUpVoteEntity questionUpVoteEntity) {
        return QuestionVoteResponse.builder()
                .id(questionUpVoteEntity.getId())
                .questionId(questionUpVoteEntity.getQuestion().getId())
                .userId(questionUpVoteEntity.getUser().getId())
                .name(questionUpVoteEntity.getUser().getFirstName() + " " + questionUpVoteEntity.getUser().getLastName())
                .createdAt(questionUpVoteEntity.getCreatedAt())
                .build();
    }
}
