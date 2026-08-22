package com.fitness.activityservice.controller;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.service.ActivityService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController
{

    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);
    private ActivityService activityService;
    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request, @RequestHeader("X-User-Id") String userId){
     if(userId!=null){
         request.setUserId(userId);
     }
      return ResponseEntity.ok(activityService.trackActivity(request));
    }
        @GetMapping
        public ResponseEntity<List<ActivityResponse>> getUserActivity(@RequestHeader("X-User-Id")String id){
            logger.info(id);
            return ResponseEntity.ok(activityService.getUserActivity(id));
        }

@GetMapping("/{id}")
public ResponseEntity<ActivityResponse> getActivity(@PathVariable String id){
    return ResponseEntity.ok(activityService.getActivity(id));
}
@Bean
public CorsConfigurationSource corsConfigurationSource(){
      CorsConfiguration config=new CorsConfiguration();
      config.setAllowedOrigins(List.of("http://localhost:5173"));
      config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTION"));
      config.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","X-User-Id"));
      config.setAllowCredentials(true);
      UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/api/**",config);
      return  source;
    }
}
