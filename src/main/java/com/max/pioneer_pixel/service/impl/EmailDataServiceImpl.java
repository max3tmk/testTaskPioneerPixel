package com.max.pioneer_pixel.service.impl;

import com.max.pioneer_pixel.dao.EmailDataDao;
import com.max.pioneer_pixel.dao.UserDao;
import com.max.pioneer_pixel.model.EmailData;
import com.max.pioneer_pixel.model.User;
import com.max.pioneer_pixel.service.EmailDataService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailDataServiceImpl implements EmailDataService {

    private final EmailDataDao emailDataDao;
    private final UserDao userDao;

    @Override
    @Transactional
    public EmailData addEmailData(Long userId, String email) {
        Optional<User> optionalUser = userDao.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + userId + " not found");
        }

        User user = optionalUser.get();

        EmailData emailData = new EmailData();
        emailData.setUser(user); // устанавливаем объект User
        emailData.setEmail(email);

        return emailDataDao.save(emailData);
    }

    @Override
    public List<EmailData> getEmailsByUserId(Long userId) {
        return emailDataDao.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteEmailData(Long emailDataId) {
        emailDataDao.deleteById(emailDataId);
    }
}
