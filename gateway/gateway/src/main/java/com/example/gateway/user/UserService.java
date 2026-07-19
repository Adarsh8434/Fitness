package com.example.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

     private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId ){

        return userServiceWebClient.get()
                .uri("/api/users/{userId}/validate",userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class,exception ->{
                    if(exception.getStatusCode()== HttpStatus.NOT_FOUND)
                        return Mono.error(new RuntimeException("User Not Found :" + userId));
                    else if (exception.getStatusCode()==HttpStatus.BAD_REQUEST) {
                        Mono.error(new RuntimeException("Invalid Request: " + userId));
                    } return  Mono.error(new RuntimeException("Unexpected error occurs"+exception.getMessage()));
                });

    }

    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
          log.info("Calling User Registration API for user ID", registerRequest.getEmail());

            return userServiceWebClient.post()
              .uri("/api/users/register")
                    .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class,exception ->{
                    if(exception.getStatusCode()== HttpStatus.BAD_REQUEST)
                        return Mono.error(new RuntimeException("BAD REQUEST :" + exception.getMessage()));
                    else if (exception.getStatusCode()==HttpStatus.INTERNAL_SERVER_ERROR) {
                        Mono.error(new RuntimeException("IINTERNAL SERVER ERROR: " + exception.getMessage()));
                    } return  Mono.error(new RuntimeException("Unexpected error occurs"+exception.getMessage()));
                });
    }
}

