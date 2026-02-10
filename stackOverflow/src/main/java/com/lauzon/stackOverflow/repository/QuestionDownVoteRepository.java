package com.lauzon.stackOverflow.repository;

import com.lauzon.stackOverflow.entity.QuestionDownVoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionDownVoteRepository extends JpaRepository<QuestionDownVoteEntity, Long> {

    Optional<QuestionDownVoteEntity> findByQuestionIdAndUserId(Long questionId, Long userId);

    Page<QuestionDownVoteEntity> findAllByQuestionId(Long questionId, Pageable pageable);

    List<QuestionDownVoteEntity> findAllByQuestionId(Long questionId);

    Long countAllByQuestionId(Long questionId);
}
