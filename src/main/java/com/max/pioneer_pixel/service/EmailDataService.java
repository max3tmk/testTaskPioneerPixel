package com.max.pioneer_pixel.service;

import com.max.pioneer_pixel.model.EmailData;

import java.util.List;

public interface EmailDataService {

    EmailData addEmailData(Long userId, String email);

    List<EmailData> getEmailsByUserId(Long userId);

    void deleteEmailData(Long emailDataId);
}
