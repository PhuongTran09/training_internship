package com.example.backend;

// import org.springframework.boot.CommandLineRunner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.annotation.Bean;

// import com.example.backend.entity.User;
// import com.example.backend.repository.UserRepository;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

//    @Bean
// public CommandLineRunner initUser(UserRepository userRepository) {
//     return args -> {
//         createOrUpdate(userRepository, "admin", "admin123", "Admin", "System", "admin@example.com");
//         createOrUpdate(userRepository, "user1", "password1", "User", "One", "user1@example.com");
//         createOrUpdate(userRepository, "testuser", "testpass", "Test", "User", "testuser@example.com");
//         createOrUpdate(userRepository, "testuser2", "testpass2", "Test2", "User2", "testuser2@example.com");
//         createOrUpdate(userRepository, "testuser3", "testpass3", "Test3", "User3", "testuser3@example.com");
//         createOrUpdate(userRepository, "testuser4", "testpass4", "Test4", "User4", "testuser4@example.com");
//     };
}

// private void createOrUpdate(UserRepository userRepository, String username, String password, String firstName, String lastName, String email) {
//     User existingUser = userRepository.findByUsername(username).orElse(null);
//     if (existingUser != null) {
//         // Update user nếu tồn tại
//         existingUser.setPassword(password);
//         existingUser.setFirstName(firstName);
//         existingUser.setLastName(lastName);
//         existingUser.setEmail(email);
//         userRepository.save(existingUser);
//     } else {
//         // Insert mới nếu chưa có
//         userRepository.save(new User(null, username, password, firstName, lastName, email));
//     }
// }

// }
