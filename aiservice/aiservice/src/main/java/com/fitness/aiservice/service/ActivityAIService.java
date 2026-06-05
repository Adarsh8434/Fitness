package com.fitness.aiservice.service;


import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {
    private final GeminiService geminiService;

    public String generateRecommnedation(Activity activity){
      String prompt = createPromptForActivity(activity);
      String aiResponse=geminiService.getAnswer(prompt);
      log.info("Response from AI : {}", aiResponse);
      return aiResponse;
    }
    private String createPromptForActivity(Activity activity) {
        return String.format("""
        You are an expert fitness coach and exercise physiologist.

        Analyze the following fitness activity and return ONLY valid JSON.
        Do not include markdown, code blocks, or any text outside the JSON response.

        Return the response in this exact format:

        {
          "analysis": {
            "overall": "string",
            "pace": "string",
            "heartRate": "string",
            "caloriesBurned": "string"
          },
          "improvements": [
            {
              "area": "string",
              "recommendation": "string"
            }
          ],
          "suggestions": [
            {
              "workout": "string",
              "description": "string"
            }
          ],
          "safety": [
            "string"
          ]
        }

        Fitness Activity Details:

        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s

        Analysis Requirements:
        1. Evaluate overall performance.
        2. Analyze pacing efficiency.
        3. Analyze heart rate performance if heart rate data is available.
        4. Evaluate calorie expenditure.
        5. Provide 3-5 actionable improvement recommendations.
        6. Suggest 3 suitable workouts to improve future performance.
        7. Include 2-4 safety recommendations based on the activity.
        8. Keep recommendations practical and personalized to the provided metrics.
        9. Return only valid JSON.
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }

//    private String createPromptForActivity(Activity activity) {
//      return String.format(""""Analyze this fitness activity and provide detailed recommendations in the following"
//        {
//            "analysis": {
//                "overall": "Overall analysis here",
//                "pace" : "Pace analysis here",
//                "heartRate": "Heart Rate analysis here" ,
//                "caloriesBurned": "Calories analysis here"
//        },
//        "improvements":[
//          {
//           "area": "Area name"
//           "recommendation": "Detailed recommendation"
//          }
//        ],
//        "Suggestions":[
//          {
//          "workout" : "Workout name",
//          "description": "Detailed workout description"
//          }
//        ],
//        "safety":[
//           "Safety point 1",
//           "Safety point 2"
//        ]
//        }
//        Analyze this activity:
//        Activity Type: %s
//        Duration: %d minutes
//        Calories Burned: %d
//        Additional Metrics: %s
//
//        ""
//    }
}
