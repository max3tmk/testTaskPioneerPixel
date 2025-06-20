package com.max.pioneer_pixel.service;

import com.max.pioneer_pixel.model.PhoneData;

import java.util.List;

public interface PhoneDataService {

    PhoneData addPhoneData(Long userId, String phoneNumber);

    List<PhoneData> getPhonesByUserId(Long userId);

    void deletePhoneData(Long phoneDataId);
}
