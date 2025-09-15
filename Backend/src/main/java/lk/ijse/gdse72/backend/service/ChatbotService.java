package lk.ijse.gdse72.backend.service;

import lk.ijse.gdse72.backend.dto.ChatResponse;
import lk.ijse.gdse72.backend.entity.User;

public interface ChatbotService {
    ChatResponse processQuery(String query, User user);
}