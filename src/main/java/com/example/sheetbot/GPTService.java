package com.example.sheetbot;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GPTService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final ExcelCourseService courseService;

    public GPTService(ExcelCourseService courseService) {
        this.courseService = courseService;
    }

    public String processUserQuery(String question) throws Exception {
        List<Course> courseList = courseService.getAllCourses();

        String prompt = """
                You are CourseBot 🤖, a helpful AI assistant that helps users find relevant online courses.

                Here is a list of available courses (in CSV format):
                Course Name,Website,Link
                """ + courseList.stream()
                .map(c -> c.getName() + "," + c.getSite() + "," + c.getLink())
                .collect(Collectors.joining("\n")) + """

                Instructions:
                1. When a user asks a question, match their intent to course names. Use:
                   - Case-insensitive comparison
                   - Fuzzy matching (like “pytun” → “Python”)
                   - Synonyms (like “AI” = “Artificial Intelligence”)
                2. If one or more courses match, return them in this format:

                Course Name: Course Name
                Website: [website]  
                Go to Course: [link]
                
                   Repeat this for **every matching course**, not just the first one.

                    🔔 IMPORTANT:  
                    If the user's request matches multiple courses (like courses starting with a certain letter or related to a keyword), list **all matching courses** clearly. Do **not stop at the first match**.  
                    For example:  
                    If the user asks: "show all courses starting with J", and there are 3 courses starting with J, list all 3.

                    If the list is long, you can say: "Here are some of them:" and ask if they want more.
                3. If no course matches, politely let the user know and suggest they try a different topic — do not repeat the same sentence every time.
        		4. If the question is clearly off-topic (like about singing, cricket, weather, etc.), remind the user that you only help with education-related topics (like programming, cloud, AI, etc.), in a warm and polite tone.
                5. If the user replies with "no", "not interested", "nothing", "thanks", etc.:
                 - If it's a rejection of a specific course suggestion, end the session politely:  
                   e.g., "Alright! Let me know if you need help later 😊"
                 - If it's part of a broader query (e.g., "no I don’t want blockchain"), interpret the rest and suggest alternatives:  
                   e.g., "Got it! Maybe you'd be interested in Python, Data Science, or Cloud courses instead?"

                6. Advanced Filters:
                 - If the user asks for courses on a specific platform (e.g., LinkedIn, LMS), only return those.
                 - If the user asks for platforms or websites, list all distinct platform names from the Excel (no duplicates).
                 - If the user asks for courses starting with a certain letter (e.g., "courses starting with A"), return only those.
                 
                 7. Maintain a friendly, supportive, and natural tone throughout.

               
                User Question:
                """ + question + """

                Answer:
                """;

        return callOpenRouter(prompt);
    }

    private String callOpenRouter(String prompt) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        JSONObject json = new JSONObject();
        json.put("model", "openai/gpt-3.5-turbo");
        json.put("messages", new JSONArray()
                .put(new JSONObject().put("role", "system").put("content", "You are CourseBot, a helpful course recommendation assistant."))
                .put(new JSONObject().put("role", "user").put("content", prompt)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // ✅ LOG raw response
        System.out.println("GPT RAW RESPONSE: " + response.body());

        // 🔐 Try parsing safely
        JSONObject jsonResponse = new JSONObject(response.body());
        if (!jsonResponse.has("choices")) {
            throw new Exception("Invalid response from GPT API: " + response.body());
        }

        return jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim();
    }

}
