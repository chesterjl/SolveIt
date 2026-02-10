package com.lauzon.stackOverflow.repository;

import com.lauzon.stackOverflow.entity.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    Optional<QuestionEntity> findByIdAndUserId(Long questionId, Long userId);

    Page<QuestionEntity> findAllByUserId(Long userId, Pageable pageable);

    Page<QuestionEntity> findAllQuestionByUserId(Long userId, Pageable pageable);

    Page<QuestionEntity> findByTitleContainingIgnoreCase(
            String titleKeyword, Pageable pageable);

    @Query("SELECT COUNT(q) FROM QuestionEntity q WHERE q.user.id = :userId")
    Long countAllQuestionsByUserId(@Param("userId") Long userId);

    Page<QuestionEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // Get popular questions based on likes and unlikes of question
    @Query("SELECT q FROM QuestionEntity q " +
            "LEFT JOIN q.upvotes u " +
            "LEFT JOIN q.downvotes d " +
            "GROUP BY q.id " +
            "ORDER BY COUNT(DISTINCT u.id) DESC, COUNT(DISTINCT d.id) ASC")
    Page<QuestionEntity> findPopularQuestions(Pageable pageable);

}
