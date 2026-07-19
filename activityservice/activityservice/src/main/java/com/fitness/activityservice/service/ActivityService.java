package com.fitness.activityservice.service;

import com.fitness.activityservice.repository.ActivityRepository;
import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    private final ActivityRepository activityRepository;
   private final UserValidationService userValidationService;
   private final RabbitTemplate rabbitTemplate;
   @Value("${rabbitmq.exchange.name}")
   private String exchange;

   @Value("${rabbitmq.routing.key}")
   private String routing;



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

      Activity saveActivity = activityRepository.save(activity);

      // Publish to RabbitMq got AI proceesing
        try{
           rabbitTemplate.convertAndSend(exchange,routing,saveActivity);
        }catch(Exception e){
            log.error("Failed to publish activity"+e);
        }
        System.out.println("Saved Activity = " + saveActivity);
        System.out.println("Saved Activity ID = " + saveActivity.getId());
      return mapToResponse(saveActivity);

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

    public ActivityResponse getActivity(String id) {
        Activity activity=activityRepository.findFirstByUserId(id)
                .orElseThrow(()->new RuntimeException("Activity not found with id "+ id));
        return mapToResponse(activity);
    }
}
