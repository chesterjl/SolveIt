package com.lauzon.stackOverflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionVoteResponse {

    private Long id;
    private Long questionId;
    private Long userId;
    private String name;
    private LocalDateTime createdAt;

}

