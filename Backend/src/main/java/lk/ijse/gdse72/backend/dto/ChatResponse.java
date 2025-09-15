package lk.ijse.gdse72.backend.dto;

import java.util.List;
import java.util.Map;

public class ChatResponse {
    private String response;
    private boolean needsClarification;
    private List<Suggestion> suggestions;

    public ChatResponse() {}

    public ChatResponse(String response, boolean needsClarification) {
        this.response = response;
        this.needsClarification = needsClarification;
    }

    // Getters and setters
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public boolean isNeedsClarification() { return needsClarification; }
    public void setNeedsClarification(boolean needsClarification) { this.needsClarification = needsClarification; }

    public List<Suggestion> getSuggestions() { return suggestions; }
    public void setSuggestions(List<Suggestion> suggestions) { this.suggestions = suggestions; }
}