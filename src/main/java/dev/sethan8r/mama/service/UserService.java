package dev.sethan8r.mama.service;

import dev.sethan8r.mama.dto.ChangePasswordRequest;
import dev.sethan8r.mama.dto.ChangeUsernameRequest;
import dev.sethan8r.mama.dto.UserResponseDto;
import dev.sethan8r.mama.exception.AlreadyExistException;
import dev.sethan8r.mama.exception.InvalidRequestException;
import dev.sethan8r.mama.exception.NotFoundException;
import dev.sethan8r.mama.mapper.UserMapper;
import dev.sethan8r.mama.model.User;
import dev.sethan8r.mama.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResponseDto getUserById(Long id) {
        log.info("Поиск пользователя с id={}", id);

        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с ID " + id + " не найден"));

        return userMapper.toUserResponseDto(user);
    }

    @Transactional
    public void updatePassword(Long id, ChangePasswordRequest dto) {
        log.info("Заменя пароля пользователем с id={}", id);

        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с ID " + id + " не найден"));

        if(!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new InvalidRequestException("Старый пароль не прошел проверку");
        }

        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new InvalidRequestException("Пароли не совпадают");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void updateUsername(Long id, ChangeUsernameRequest dto) {
        log.info("Заменя username пользователем с id={}", id);

        if(userRepository.existsByUsername(dto.username())) {
            throw new AlreadyExistException("Пользователь с username \"" + dto.username() + "\" уже существует");
        }

        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с ID " + id + " не найден"));
        user.setUsername(dto.username());

        userRepository.save(user);
    }
}
