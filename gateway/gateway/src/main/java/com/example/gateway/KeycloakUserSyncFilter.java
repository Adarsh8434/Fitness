package com.example.gateway;

import com.example.gateway.user.RegisterRequest;
import com.example.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange , WebFilterChain chain){

      String token=exchange.getRequest().getHeaders().getFirst("Authorization");
      String userId=exchange.getRequest().getHeaders().getFirst("X-User-Id");
      RegisterRequest registerRequest=getUserDetails(token);
     if(userId==null){
         userId=registerRequest.getKeyCloakId();
     }
      if(userId!= null && token!=null) {
          String finalUserId = userId;
          return userService.validateUser(userId)
              .flatMap(exist->{
                if(!exist) {
                    if(registerRequest!=null){
                        return userService.registerUser(registerRequest)
                                .then(Mono.empty());
                    }
                    else return Mono.empty();
                }else{
                    log.info("User Already exist, Skipping sync");
                    return Mono.empty();
                }
              })
              .then(Mono.defer(()->{
                ServerHttpRequest mutatedRequest = exchange.getRequest()
                        .mutate()
                        .header("X-user-Id", finalUserId)
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
              }));
      }
      log.info("checking ");
         return chain.filter(exchange);

    }

    private RegisterRequest getUserDetails(String token) {
      try{
      String tokenWithoutBearer = token.replace("Bearer ","").trim();
          SignedJWT signedJWT=SignedJWT.parse(tokenWithoutBearer);
          JWTClaimsSet claims= signedJWT.getJWTClaimsSet();

          RegisterRequest registerRequest=new RegisterRequest();
          registerRequest.setFirstName(claims.getStringClaim("First_name"));
          registerRequest.setEmail(claims.getStringClaim("email"));
          registerRequest.setKeyCloakId(claims.getStringClaim("sub"));
          registerRequest.setLastName(claims.getStringClaim("last_name"));
          registerRequest.setPassword("dummy@123");
          return registerRequest;

      }catch(Exception e) {
        e.printStackTrace();
        return null;
        }
    }
}
