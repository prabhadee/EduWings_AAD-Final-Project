package lk.ijse.gdse72.backend.dto;

import java.util.Map;

public class Suggestion {
    private String text;
    private String action;
    private Map<String, Object> data;

    public Suggestion() {}

    // Getters and setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}