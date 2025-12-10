package dev.sethan8r.mama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO) //красивая структура в PAGE
@SpringBootApplication
public class MamaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MamaApplication.class, args);
    }

//    public static void main(String[] args) {
//        ApplicationContext context = SpringApplication.run(MamaApplication.class, args);
//
//        BCryptPasswordEncoder passwordEncoder = context.getBean(BCryptPasswordEncoder.class);
//        String rawPassword = "admin";
//        String encodedPassword = passwordEncoder.encode(rawPassword);
//
//        System.out.println("Encoded password: " + encodedPassword);
//    }

}
