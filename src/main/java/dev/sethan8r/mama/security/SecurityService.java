package dev.sethan8r.mama.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecurityService {

    public CustomUserDetails getCurrentUser() {
        log.info("Достаем пользователя из контекста");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AccessDeniedException("Пользователь не авторизован");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails user) {
            log.info("Достали пользователя с id={}", user.getId());

            return user;
        }

        throw new AccessDeniedException("Не удалось определить пользователя");
    }
}
