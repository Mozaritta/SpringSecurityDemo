package com.example.demo;

import com.example.demo.Models.Role;
import com.example.demo.Models.User;
import com.example.demo.Services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean// we need to add this annotation so that spring pick it and run it
    CommandLineRunner run(UserService userService){
        return args -> {
            userService.saveRole(new Role(null, "ROLE_USER"));
            userService.saveRole(new Role(null, "ROLE_ADMIN"));
            userService.saveRole(new Role(null, "ROLE_MANAGER"));
            userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

            userService.saveUser(new User(null, "John Doe", "John98", "azerty", new ArrayList<>()));
            userService.saveUser(new User(null, "Spring User", "helloworld", "ac6cf692-0bb9-4800-9b08-3c60f3fdfd82", new ArrayList<>()));
            userService.saveUser(new User(null, "Test", "test", "hello", new ArrayList<>()));

            userService.affectRole("helloworld", "ROLE_USER");
            userService.affectRole("helloworld", "ROLE_MANAGER");
            userService.affectRole("John98", "ROLE_ADMIN");
            userService.affectRole("test", "ROLE_SUPER_ADMIN");
            userService.affectRole("test", "ROLE_ADMIN");
        };
    }

}
