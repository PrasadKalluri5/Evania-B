package com.example.sheetbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/gpt")
public class CourseController {

    @Autowired
    private GPTService gptService;

    @GetMapping("/search")
    public String searchCourses(@RequestParam String query) {
        try {
            return gptService.processUserQuery(query);
        } catch (Exception e) {
            return "⚠️ Error: " + e.getMessage();
        }
    }

    @GetMapping("/ping")
    public String ping() {
        return "✅ GPT CourseBot is running!";
    }
}
