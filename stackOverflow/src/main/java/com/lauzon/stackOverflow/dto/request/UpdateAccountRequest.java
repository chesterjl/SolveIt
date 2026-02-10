package com.lauzon.stackOverflow.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpdateAccountRequest {

    private String username;
    private String bio;
    private String firstName;
    private String lastName;
    private String password;
    private String confirmationPassword;

}
