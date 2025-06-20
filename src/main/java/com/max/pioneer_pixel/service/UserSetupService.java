package com.max.pioneer_pixel.service;

import com.max.pioneer_pixel.dao.AccountDao;
import com.max.pioneer_pixel.dao.EmailDataDao;
import com.max.pioneer_pixel.dao.PhoneDataDao;
import com.max.pioneer_pixel.dao.UserDao;
import com.max.pioneer_pixel.model.Account;
import com.max.pioneer_pixel.model.EmailData;
import com.max.pioneer_pixel.model.PhoneData;
import com.max.pioneer_pixel.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class UserSetupService {

    private final UserDao userDao;
    private final EmailDataDao emailDataDao;
    private final PhoneDataDao phoneDataDao;
    private final AccountDao accountDao;
    private final Random random = new Random();

    public UserSetupService(UserDao userDao, EmailDataDao emailDataDao, PhoneDataDao phoneDataDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.emailDataDao = emailDataDao;
        this.phoneDataDao = phoneDataDao;
        this.accountDao = accountDao;
    }

    @Transactional
    public void createUsersWithContacts(List<User> users) {
        for (User user : users) {
            User savedUser = userDao.save(user);

            EmailData email = new EmailData();
            email.setUser(savedUser);  // <-- Здесь передаем объект User
            email.setEmail(savedUser.getName().toLowerCase() + "@example.com");
            emailDataDao.save(email);

            PhoneData phone = new PhoneData();
            phone.setUser(savedUser);  // <-- Здесь тоже объект User
            phone.setPhone("+3752" + (1000000 + random.nextInt(9000000)));
            phoneDataDao.save(phone);

            Account account = new Account();
            account.setUser(savedUser);
            account.setBalance(BigDecimal.valueOf(1000));
            account.setInitialBalance(BigDecimal.valueOf(1000));
            accountDao.save(account);
        }
    }
}
