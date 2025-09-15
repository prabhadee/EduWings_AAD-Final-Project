package lk.ijse.gdse72.backend.controller;

import lk.ijse.gdse72.backend.dto.ChatRequest;
import lk.ijse.gdse72.backend.dto.ChatResponse;
import lk.ijse.gdse72.backend.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/query")
    public ResponseEntity<ChatResponse> processChatQuery(@RequestBody ChatRequest chatRequest) {
        try {
            // For testing, return a simple response
            ChatResponse response = new ChatResponse();
            response.setResponse("I received your message: " + chatRequest.getMessage());
            response.setNeedsClarification(false);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setResponse("Sorry, I encountered an error processing your request.");
            errorResponse.setNeedsClarification(false);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Chat endpoint is working!");
    }
}