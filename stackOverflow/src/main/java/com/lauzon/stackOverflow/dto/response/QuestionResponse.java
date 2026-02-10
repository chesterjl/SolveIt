package com.lauzon.stackOverflow.dto.response;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionResponse {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String imagePublicId;

    private Long userId;
    private String name;
    private String username;

    private boolean isLiked;
    private boolean isUnliked;
    private Long answers;
    private Long likes;
    private Long unlikes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
