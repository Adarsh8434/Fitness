package com.fitness.aiservice.service;


import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

private final ActivityAIService activityAIService;
private final RecommendationRepository recommendationRepository;
    @RabbitListener(queues ="activity.queue" )
  public void processActivity(Activity activity){
   log.info("Received activity for processing : {} ",activity.getId());
//   log.info("Generated Recommendation : {} ",activityAIService.generateRecommnedation(activity));
        try {
            Recommendation recommendation = activityAIService.generateRecommnedation(activity);
            recommendationRepository.save(recommendation);
            log.info("Generated Recommendation : {}", recommendation);
        } catch (Exception e) { 
            log.error("Failed to process activity {}: {}", activity.getId(), e.getMessage());
        }

  }
}
