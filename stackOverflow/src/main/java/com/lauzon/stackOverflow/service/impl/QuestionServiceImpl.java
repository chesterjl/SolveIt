package com.lauzon.stackOverflow.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lauzon.stackOverflow.dto.request.QuestionRequest;
import com.lauzon.stackOverflow.dto.response.QuestionResponse;
import com.lauzon.stackOverflow.entity.*;
import com.lauzon.stackOverflow.exception.ResourceNotFoundException;
import com.lauzon.stackOverflow.exception.UserNotFoundException;
import com.lauzon.stackOverflow.repository.*;
import com.lauzon.stackOverflow.service.QuestionService;
import com.lauzon.stackOverflow.util.UtilMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final UtilMethod utilMethod;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final QuestionUpVoteRepository questionUpVoteRepository;
    private final QuestionDownVoteRepository questionDownVoteRepository;
    private final Cloudinary cloudinary;

    @Override
    public QuestionResponse createQuestion(QuestionRequest questionRequest) throws IOException {
        String imageUrl = null;
        String imagePublicId = null;

        // Upload image if provided
        if (questionRequest.getImageFile() != null && !questionRequest.getImageFile().isEmpty()) {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    questionRequest.getImageFile().getBytes(),
                    ObjectUtils.asMap("resource_type", "image")
            );
            imageUrl = (String) uploadResult.get("secure_url");
            imagePublicId = (String) uploadResult.get("public_id");
        }

        UserEntity user = utilMethod.getCurrentUser();

        QuestionEntity question = convertToEntity(questionRequest, imageUrl, imagePublicId, user);
        question = questionRepository.save(question);

        return convertToResponseSafe(question);
    }

    @Override
    public QuestionResponse updateQuestion(QuestionRequest questionRequest, Long questionId) throws IOException {
        UserEntity user = utilMethod.getCurrentUser();
        QuestionEntity existingQuestion = questionRepository.findByIdAndUserId(questionId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("Question not found or you are not authorized to update this question"));

        existingQuestion.setTitle(questionRequest.getTitle());
        existingQuestion.setDescription(questionRequest.getDescription());

        if (questionRequest.getImageFile() != null && !questionRequest.getImageFile().isEmpty()) {
            if (existingQuestion.getImagePublicId() != null) {
                cloudinary.uploader().destroy(existingQuestion.getImagePublicId(), ObjectUtils.asMap("resource_type", "image"));
            }

            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    questionRequest.getImageFile().getBytes(),
                    ObjectUtils.asMap("resource_type", "image")
            );

            existingQuestion.setImageUrl((String) uploadResult.get("secure_url"));
            existingQuestion.setImagePublicId((String) uploadResult.get("public_id"));
        }

        existingQuestion = questionRepository.save(existingQuestion);

        return convertToResponseSafe(existingQuestion);
    }

    @Override
    public void deleteQuestion(Long questionId) {
        UserEntity user = utilMethod.getCurrentUser();
        QuestionEntity question = questionRepository.findByIdAndUserId(questionId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("Question not found or you are not authorized to delete this question"));

        if (question.getImagePublicId() != null) {
            try {
                cloudinary.uploader().destroy(question.getImagePublicId(), ObjectUtils.asMap("resource_type", "image"));
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete image from Cloudinary: " + e.getMessage());
            }
        }

        List<AnswerEntity> answers = answerRepository.findAllByQuestionId(questionId);
        List<QuestionUpVoteEntity> upvotes = questionUpVoteRepository.findAllByQuestionId(questionId);
        List<QuestionDownVoteEntity> downvotes = questionDownVoteRepository.findAllByQuestionId(questionId);
        answerRepository.deleteAll(answers);
        questionUpVoteRepository.deleteAll(upvotes);
        questionDownVoteRepository.deleteAll(downvotes);

        questionRepository.delete(question);
    }

    @Override
    public Map<String, Object> viewQuestion(Long questionId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        List<AnswerEntity> answers = answerRepository.findAllByQuestion(question);

        List<Map<String, Object>> answersList = answers.stream()
                .map(answer -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("answerId", answer.getId());
                    map.put("description", answer.getDescription());
                    map.put("createdAt", answer.getCreatedAt());
                    map.put("updatedAt", answer.getUpdatedAt());

                    map.put("upvotes", safeList(answer.getQuestion().getUpvotes()).size());
                    map.put("downvotes", safeList(answer.getQuestion().getDownvotes()).size());

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userId", answer.getUser().getId());
                    userMap.put("name", answer.getUser().getFirstName() + " " + answer.getUser().getLastName());
                    userMap.put("username", Optional.ofNullable(answer.getUser().getUsername())
                            .orElse(answer.getUser().getFirstName().toLowerCase()));
                    map.put("user", userMap);

                    return map;
                }).collect(Collectors.toList());

        Map<String, Object> questionData = new HashMap<>();
        questionData.put("questionId", question.getId());
        questionData.put("title", question.getTitle());
        questionData.put("description", question.getDescription());
        questionData.put("imageUrl", question.getImageUrl());
        questionData.put("createdAt", question.getCreatedAt());
        questionData.put("updatedAt", question.getUpdatedAt());

        Map<String, Object> authorMap = new HashMap<>();
        authorMap.put("userId", question.getUser().getId());
        authorMap.put("name", question.getUser().getFirstName() + " " + question.getUser().getLastName());
        authorMap.put("username", Optional.ofNullable(question.getUser().getUsername())
                .orElse(question.getUser().getFirstName().toLowerCase()));
        authorMap.put("email", question.getUser().getEmail());

        questionData.put("author", authorMap);
        questionData.put("answers", answersList);
        questionData.put("totalAnswers", answersList.size());

        return questionData;
    }

    @Override
    public Map<String, Object> viewAllQuestions(int page, int size) {
        UserEntity user = utilMethod.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QuestionEntity> questionsPage = questionRepository.findAllByUserId(user.getId(), pageable);

        List<Map<String, Object>> questionsList = questionsPage.getContent().stream()
                .map(q -> {
                    Long totalAnswers = answerRepository.countAllAnswersByQuestionId(q.getId());
                    Long totalLikes = questionUpVoteRepository.countAllByQuestionId(q.getId());
                    Long totalUnlikes = questionDownVoteRepository.countAllByQuestionId(q.getId());

                    boolean isLiked = safeList(q.getUpvotes()).stream()
                            .anyMatch(v -> v.getUser().getId().equals(user.getId()));
                    boolean isUnLiked = safeList(q.getDownvotes()).stream()
                            .anyMatch(v -> v.getUser().getId().equals(user.getId()));

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", q.getId());
                    map.put("title", q.getTitle());
                    map.put("description", q.getDescription());
                    map.put("imageUrl", q.getImageUrl());
                    map.put("createdAt", q.getCreatedAt());
                    map.put("answers", totalAnswers);
                    map.put("likes", totalLikes);
                    map.put("unlikes", totalUnlikes);
                    map.put("liked", isLiked);
                    map.put("unliked", isUnLiked);

                    return map;
                }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", questionsList);
        response.put("totalPages", questionsPage.getTotalPages());
        response.put("totalElements", questionsPage.getTotalElements());
        response.put("currentPage", page);

        return response;
    }

    @Override
    public Page<QuestionResponse> feeds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QuestionEntity> questions = questionRepository.findAll(pageable);
        return questions.map(this::convertToResponseSafe);
    }

    @Override
    public Page<QuestionResponse> getRecentQuestions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuestionEntity> questions = questionRepository.findAllByOrderByCreatedAtDesc(pageable);
        return questions.map(this::convertToResponseSafe);
    }

    @Override
    public Page<QuestionResponse> getPopularQuestions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuestionEntity> questions = questionRepository.findPopularQuestions(pageable);
        return questions.map(this::convertToResponseSafe);
    }

    private QuestionEntity convertToEntity(QuestionRequest request, String imageUrl, String imagePublicId, UserEntity user) {
        return QuestionEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(imageUrl)
                .imagePublicId(imagePublicId)
                .user(user)
                .build();
    }

    private QuestionResponse convertToResponseSafe(QuestionEntity question) {
        UserEntity currentUser = utilMethod.getCurrentUser();

        List<QuestionUpVoteEntity> upvotes = safeList(question.getUpvotes());
        List<QuestionDownVoteEntity> downvotes = safeList(question.getDownvotes());

        boolean isLiked = upvotes.stream().anyMatch(v -> v.getUser().getId().equals(currentUser.getId()));
        boolean isUnliked = downvotes.stream().anyMatch(v -> v.getUser().getId().equals(currentUser.getId()));

        Long answers = answerRepository.countAllAnswersByQuestionId(question.getId());
        Long likes = questionUpVoteRepository.countAllByQuestionId(question.getId());
        Long unlikes = questionDownVoteRepository.countAllByQuestionId(question.getId());

        String username = Optional.ofNullable(question.getUser().getUsername())
                .orElse("@" + question.getUser().getFirstName().toLowerCase().replace(" ", ""));

        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .description(question.getDescription())
                .imageUrl(question.getImageUrl())
                .imagePublicId(question.getImagePublicId())
                .name(question.getUser().getFirstName() + " " + question.getUser().getLastName())
                .username(username)
                .answers(answers)
                .isLiked(isLiked)
                .isUnliked(isUnliked)
                .likes(likes)
                .unlikes(unlikes)
                .userId(question.getUser().getId())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }

    // Utility: return empty list if null
    private <T> List<T> safeList(List<T> list) {
        return (list == null) ? Collections.emptyList() : list;
    }
}
