package dev.sethan8r.mama.security;

import dev.sethan8r.mama.mapper.UserMapper;
import dev.sethan8r.mama.model.User;
import dev.sethan8r.mama.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Авторизация пользователем с username \"{}\"", username);

        User user = userRepository.findByUsername(username).orElseThrow(()
                -> new UsernameNotFoundException("Пользователь с таким username не найден"));

        return userMapper.toCustomUserDetails(user);
    }
}
