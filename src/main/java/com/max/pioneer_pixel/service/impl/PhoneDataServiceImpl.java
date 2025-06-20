package com.max.pioneer_pixel.service.impl;

import com.max.pioneer_pixel.dao.PhoneDataDao;
import com.max.pioneer_pixel.dao.UserDao;
import com.max.pioneer_pixel.model.PhoneData;
import com.max.pioneer_pixel.model.User;
import com.max.pioneer_pixel.service.PhoneDataService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneDataServiceImpl implements PhoneDataService {

    private final PhoneDataDao phoneDataDao;
    private final UserDao userDao;

    @Override
    @Transactional
    public PhoneData addPhoneData(Long userId, String phone) {
        Optional<User> optionalUser = userDao.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + userId + " not found");
        }

        PhoneData phoneData = new PhoneData();
        phoneData.setPhone(phone);
        phoneData.setUser(optionalUser.get());

        return phoneDataDao.save(phoneData);
    }

    @Override
    public List<PhoneData> getPhonesByUserId(Long userId) {
        return phoneDataDao.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deletePhoneData(Long phoneDataId) {
        phoneDataDao.deleteById(phoneDataId);
    }
}
