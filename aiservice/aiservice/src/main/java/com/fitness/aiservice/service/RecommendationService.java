package com.fitness.aiservice.service;


import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
 private final RecommendationRepository recommendationRepository;


    public List<Recommendation> getUserRecommendation(String userId) {
    return  recommendationRepository.findByUserId(userId);
    }

    public Recommendation getActivityRecommendation(String activityId) {
      return recommendationRepository.findByActivityId(activityId)
              .orElseThrow(()->new RuntimeException("No Recommendation found for this activity : "+activityId));
    }
}
