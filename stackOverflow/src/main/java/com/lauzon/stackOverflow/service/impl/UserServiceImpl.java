package com.lauzon.stackOverflow.service.impl;

import com.lauzon.stackOverflow.entity.UserEntity;
import com.lauzon.stackOverflow.repository.AnswerRepository;
import com.lauzon.stackOverflow.repository.QuestionRepository;
import com.lauzon.stackOverflow.repository.UserRepository;
import com.lauzon.stackOverflow.service.UserService;
import com.lauzon.stackOverflow.util.UtilMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final UtilMethod utilMethod;

    @Override
    public Map<String, Object> getUserInfo() {
        UserEntity user = utilMethod.getCurrentUser();
        Long answers = answerRepository.countAllAnswersByUserId(user.getId());
        Long questions = questionRepository.countAllQuestionsByUserId(user.getId());
        return convertToDict(answers, questions, user);
    }

    private Map<String, Object> convertToDict(Long answers, Long questions, UserEntity user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getFirstName() + " " + user.getLastName());
        userInfo.put("email", user.getEmail());
        userInfo.put("username", user.getUsername() == null
                ? "@" + user.getFirstName().toLowerCase().replace(" ", "")
                : user.getUsername());
        userInfo.put("bio", user.getBio() == null ? "This user has no bio" : user.getBio());
        userInfo.put("answers", answers);
        userInfo.put("questions", questions);

        return Map.of("user", userInfo);
    }
}