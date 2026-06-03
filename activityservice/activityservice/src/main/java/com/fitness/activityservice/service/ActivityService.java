package com.fitness.activityservice.service;

import com.fitness.activityservice.repository.ActivityRepository;
import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
   private final UserValidationService userValidationService;
    private final MongoTemplate mongoTemplate;
    public ActivityResponse trackActivity(ActivityRequest request) {
        boolean isValidUser=userValidationService.validateUser(request.getUserId());
        if(!isValidUser) throw new RuntimeException("Invalid user:"+request.getUserId());
        Activity activity=Activity.builder()
              .userId(request.getUserId())
              .type(request.getType())
              .duration(request.getDuration())
              .caloriesBurned(request.getCaloriesBurned())
              .startTime(request.getStartTime())
              .additionalMetrics(request.getAdditionalMetrics())
              .build();
        System.out.println("Database Name = " + mongoTemplate.getDb().getName());
        System.out.println("URI = " + mongoTemplate.getMongoDatabaseFactory().getMongoDatabase().getName());
//        System.out.println(mongoTemplate.getDb().getMongoClient());
      Activity saveActivity = activityRepository.save(activity);
//        Activity saveActivity = activityRepository.save(activity);

        System.out.println("Saved Activity = " + saveActivity);
        System.out.println("Saved Activity ID = " + saveActivity.getId());
      return mapToResponse(saveActivity);

    }
    @Bean
    CommandLineRunner configCheck(Environment env) {
        return args -> {
            System.out.println("Mongo URI = "
                    + env.getProperty("spring.data.mongodb.uri"));

            System.out.println("Mongo DB = "
                    + env.getProperty("spring.data.mongodb.database"));
        };
    }
    @Bean
    CommandLineRunner verifyMongo(MongoTemplate mongoTemplate) {
        return args -> {
            System.out.println("Actual DB = " +
                    mongoTemplate.getMongoDatabaseFactory()
                            .getMongoDatabase()
                            .getName());
            System.out.println("Activity Count = " +
                    mongoTemplate.getCollection("activities")
                            .countDocuments());
        };
    }
    private ActivityResponse mapToResponse (Activity activity){
        ActivityResponse response=new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());

        return response;

    }

    public List<ActivityResponse> getUserActivity(String userId) {
       List<Activity> activities= activityRepository.findByUserId(userId);
       return activities.stream()
               .map(this:: mapToResponse)
               .collect(Collectors.toList());
    }
}
