package com.lauzon.stackOverflow.repository;

import com.lauzon.stackOverflow.entity.AnswerEntity;
import com.lauzon.stackOverflow.entity.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {

    Page<AnswerEntity> findAllByQuestionId(Long questionId, Pageable pageable);

    List<AnswerEntity> findAllByQuestionId(Long questionId);


    Optional<AnswerEntity> findByIdAndUserId(Long questionId, Long userId);

    @Query("SELECT COUNT(a) FROM AnswerEntity a WHERE a.user.id = :userId")
    Long countAllAnswersByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM AnswerEntity a WHERE a.question.id = :questionId")
    Long countAllAnswersByQuestionId(@Param("questionId") Long questionId);

    List<AnswerEntity> findAllByQuestion(QuestionEntity question);
}
