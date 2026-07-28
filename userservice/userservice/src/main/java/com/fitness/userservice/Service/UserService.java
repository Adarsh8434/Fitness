package com.fitness.userservice.Service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public UserResponse getUserProfile(String userId) {
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
        UserResponse userResponse=new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setPassword(user.getPassword());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        return userResponse;
    }

    public UserResponse register(RegisterRequest request) {
        log.info("Register called");
        log.info("Email: {}", request.getEmail());
        log.info("KeycloakId: {}", request.getKeyCloakId());
       if(userRepository.existsByEmail(request.getEmail())) {

           User existingUser= userRepository.findByEmail(request.getEmail());
           log.info("Existing user found");
           log.info("DB KeycloakId: {}", existingUser.getKeyCloakId());
           if (existingUser.getKeyCloakId() == null) {
               existingUser.setKeyCloakId(request.getKeyCloakId());
               userRepository.save(existingUser);
           }
           UserResponse userResponse=new UserResponse();
           userResponse.setId(existingUser.getId());
           userResponse.setkeyCloakId(existingUser.getKeyCloakId());
           userResponse.setEmail(existingUser.getEmail());
           userResponse.setPassword(existingUser.getPassword());
           userResponse.setFirstName(existingUser.getFirstName());
           userResponse.setLastName(existingUser.getLastName());
           userResponse.setCreatedAt(existingUser.getCreatedAt());
           userResponse.setUpdatedAt(existingUser.getUpdatedAt());

           return userResponse;
       }

        User user=new User();
       user.setEmail(request.getEmail() );
       user.setKeyCloakId(request.getKeyCloakId());
        user.setPassword(request.getPassword());
       user.setFirstName(request.getFirstName());
       user.setLastName(request.getLastName());
       User savedUser= userRepository.save(user);
        log.info("Email: {}", request.getEmail());
        log.info("KeycloakId: {}", request.getKeyCloakId());
        UserResponse userResponse=new UserResponse();
        userResponse.setkeyCloakId(savedUser.getKeyCloakId());
        userResponse.setId(savedUser.getId());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        userResponse.setUpdatedAt(savedUser.getUpdatedAt());
        return userResponse;

    }

    public boolean existByUserId(String userId) {
        log.info("Calling user validation API for userId: {}"+ userId);
        return userRepository.existsByKeyCloakId(userId);
    }
}
