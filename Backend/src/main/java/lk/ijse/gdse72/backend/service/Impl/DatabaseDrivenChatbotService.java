package lk.ijse.gdse72.backend.service.Impl;

import lk.ijse.gdse72.backend.dto.ChatResponse;
import lk.ijse.gdse72.backend.entity.User;
import lk.ijse.gdse72.backend.entity.Course;
import lk.ijse.gdse72.backend.entity.Instructor;
import lk.ijse.gdse72.backend.entity.Batch;

import lk.ijse.gdse72.backend.repository.BatchRepository;
import lk.ijse.gdse72.backend.repository.CourseRepository;
import lk.ijse.gdse72.backend.repository.InstructorRepository;
import lk.ijse.gdse72.backend.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DatabaseDrivenChatbotService implements ChatbotService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private BatchRepository batchRepository;

    private static final Map<String, String> GREETING_PATTERNS = Map.of(
            "hello", "greeting",
            "hi", "greeting",
            "hey", "greeting"
    );

    private static final Map<String, String> QUESTION_PATTERNS = Map.of(
            "course.*list", "course_list",
            "what.*courses", "course_list",
            "instructor.*info", "instructor_info",
            "batch.*schedule", "batch_schedule",
            "module.*content", "module_content",
            "price.*cost", "pricing_info"
    );

    @Override
    public ChatResponse processQuery(String query, User user) {
        String lowerQuery = query.toLowerCase();

        // Check for greetings
        for (String pattern : GREETING_PATTERNS.keySet()) {
            if (lowerQuery.contains(pattern)) {
                return new ChatResponse("Hello! How can I help you with our courses today?", false);
            }
        }

        // Check for specific question patterns
        for (Map.Entry<String, String> entry : QUESTION_PATTERNS.entrySet()) {
            if (lowerQuery.matches(".*" + entry.getKey() + ".*")) {
                return handleQuestionType(entry.getValue(), query, user);
            }
        }

        return new ChatResponse("I'm not sure how to answer that. Could you please rephrase your question " +
                "or ask about our courses, instructors, batches, or pricing?", true);
    }

    private ChatResponse handleQuestionType(String questionType, String originalQuery, User user) {
        switch (questionType) {
            case "course_list":
                List<Course> courses = courseRepository.findByIsActiveTrue();
                if (courses.isEmpty()) {
                    return new ChatResponse("We currently don't have any active courses. Please check back later.", false);
                }
                String courseList = courses.stream()
                        .map(c -> "- " + c.getCourseName() + ": " + c.getDescription())
                        .collect(Collectors.joining("\n"));
                return new ChatResponse("We offer the following courses:\n" + courseList, false);

            case "instructor_info":
                List<Instructor> instructors = instructorRepository.findAll();
                if (instructors.isEmpty()) {
                    return new ChatResponse("We don't have instructor information available at the moment.", false);
                }
                String instructorInfo = instructors.stream()
                        .map(i -> "- " + i.getName() + ": " + i.getPhone())
                        .collect(Collectors.joining("\n"));
                return new ChatResponse("Our instructors are:\n" + instructorInfo, false);

            case "batch_schedule":
                List<Batch> batches = batchRepository.findByIsActiveTrue();
                if (batches.isEmpty()) {
                    return new ChatResponse("We don't have any active batches at the moment.", false);
                }
                String batchInfo = batches.stream()
                        .map(b -> "- " + b.getBatchName() + ": Starts on " + b.getMonthlyFee() )
                        .collect(Collectors.joining("\n"));
                return new ChatResponse("Our current batches are:\n" + batchInfo, false);

            case "pricing_info":
                // You can implement pricing logic based on your database
                return new ChatResponse("Our course pricing varies by program. Most monthly subscriptions range between $50-200. " +
                        "Would you like information about a specific course?", true);

            default:
                return new ChatResponse("I need more information to answer that. Could you specify which course or batch you're asking about?", true);
        }
    }


}