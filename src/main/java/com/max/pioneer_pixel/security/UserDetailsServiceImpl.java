package com.max.pioneer_pixel.security;

import com.max.pioneer_pixel.dao.UserDao;
import com.max.pioneer_pixel.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsServiceImpl")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userDao.findByEmail(login)
                .or(() -> userDao.findByPhone(login))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + login));

        return new UserPrincipal(user, login);
    }
}
