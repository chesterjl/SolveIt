package com.lauzon.stackOverflow.repository;

import com.lauzon.stackOverflow.entity.QuestionUpVoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionUpVoteRepository extends JpaRepository<QuestionUpVoteEntity, Long> {

    Optional<QuestionUpVoteEntity> findByQuestionIdAndUserId(Long questionId, Long userId);

    @Query("SELECT v FROM QuestionUpVoteEntity v WHERE v.question.id = :questionId")
    Page<QuestionUpVoteEntity> findAllByQuestionId(@Param("questionId") Long questionId, Pageable pageable);

    List<QuestionUpVoteEntity> findAllByQuestionId(Long questionId);

    Long countAllByQuestionId(Long questionId);

}
