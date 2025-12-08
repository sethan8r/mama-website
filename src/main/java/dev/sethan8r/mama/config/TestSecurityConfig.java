package dev.sethan8r.mama.config;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;

import java.util.function.Supplier;

@Configuration
@Profile("test")
public class TestSecurityConfig {
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        return new DefaultMethodSecurityExpressionHandler() {
            @Override
            public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication, MethodInvocation mi) {
                // Позволяем всем проходить
                return super.createEvaluationContext(authentication, mi);
            }
        };
    }

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**")  // ← только эти пути обрабатывает тест-конфиг
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .build();
    }
}
