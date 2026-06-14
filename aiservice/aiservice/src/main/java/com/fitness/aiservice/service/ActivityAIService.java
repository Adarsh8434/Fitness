package com.fitness.aiservice.service;


import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {
    private final GeminiService geminiService;

    public Recommendation generateRecommnedation(Activity activity){
        log.info(">>> Calling Gemini for activity: {}", activity.getId());
        String prompt = createPromptForActivity(activity);
        String aiResponse=geminiService.getAnswer(prompt);
      log.info("Response from AI : {}", aiResponse);
      processAIResponse(activity,aiResponse);
      return processAIResponse(activity,aiResponse);
    }
    private Recommendation processAIResponse(Activity activity, String aiResponse){
           try{
               ObjectMapper mapper=new ObjectMapper();
               JsonNode rootNode =mapper.readTree(aiResponse);
               JsonNode textNode= rootNode.path("candidates")
                       .get(0)
                       .path("content")
                       .path("parts")
                       .get(0)
                       .path("text");
               String jsonContent = textNode.asText()
                       .replaceAll("```json\\n","")
                       .replaceAll("\\n```","")
                      .trim();
               log.info("Parsed response from ai : {}",jsonContent);

               JsonNode analysisJson =mapper.readTree(jsonContent);
               JsonNode analysisNode=analysisJson.path("analysis");

               StringBuilder fullAnalysis=new StringBuilder();
               addAnalysisSection(fullAnalysis,analysisNode,"overall","Overall : ");
               addAnalysisSection(fullAnalysis,analysisNode,"pace","Pace : ");
               addAnalysisSection(fullAnalysis,analysisNode,"heartRate","Heart Rate : ");
               addAnalysisSection(fullAnalysis,analysisNode,"caloriesBurned","Calories : ");

            List<String> improvements=extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions=extractSuggestions(analysisJson.path("suggestions"));
            List<String> safety=extractSafetyGuidelines(analysisJson.path("safety"));

           return Recommendation.builder().activityId(activity.getId())
                   .userId(activity.getUserId())
                   .activityId(activity.getType())
                   .recommendation(fullAnalysis.toString().trim())
                   .improvements(improvements)
                   .suggestions(suggestions)
                   .safety(safety)
                   .createdAt(LocalDateTime.now())
                   .build();
           }catch (Exception e){
               e.printStackTrace();
               return createDefaultRecommendation(activity);
           }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return    Recommendation
                .builder()
                .userId(activity.getUserId())
                .activityId(activity.getType())
                .recommendation("Unable to generate detailed Analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"

                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if (safetyNode.isArray()) {
            safetyNode.forEach(item-> safety.add(item.asText()));
            }



        return safety.isEmpty() ?
                Collections.singletonList("Follow general guidelines") :
                safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if (suggestionsNode.isArray()) {
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s", workout, description));

            });
        }
        return suggestions.isEmpty() ?
                Collections.singletonList("No specific improvements provided") :
                suggestions;
    }


    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if (improvementsNode.isArray()) {
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s", area, detail));

            });
        }
            return improvements.isEmpty() ?
                    Collections.singletonList("No specific improvements provided") :
                    improvements;


    }
    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
       if(!analysisNode.path(key).isMissingNode()){
          fullAnalysis.append(prefix)
           .append(analysisNode.path(key).asText())
                   .append("\n\n");
       }
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
