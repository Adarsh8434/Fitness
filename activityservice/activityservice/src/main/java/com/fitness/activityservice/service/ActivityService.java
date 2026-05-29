package com.fitness.activityservice.service;

import com.fitness.activityservice.repository.ActivityRepository;
import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
   private final UserValidationService userValidationService;
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
}
