package com.lauzon.stackOverflow.service;

import com.lauzon.stackOverflow.dto.request.UpdateAccountRequest;

import java.util.Map;

public interface UserService {

    Map<String, Object> getUserInfo();

    void updateUserInfo(UpdateAccountRequest request);

}
