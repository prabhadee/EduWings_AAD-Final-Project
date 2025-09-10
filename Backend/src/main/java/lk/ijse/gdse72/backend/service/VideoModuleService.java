package lk.ijse.gdse72.backend.service;

import lk.ijse.gdse72.backend.dto.VideoModuleDTO;
import java.util.List;

public interface VideoModuleService {
    VideoModuleDTO createModule(Long monthId, VideoModuleDTO dto);
    VideoModuleDTO updateModule(Long moduleId, VideoModuleDTO dto);
    void deleteModule(Long moduleId);
    VideoModuleDTO getModuleById(Long moduleId);
    List<VideoModuleDTO> getModulesByMonth(Long monthId);
    List<VideoModuleDTO> getAllModules(); // Added method to get all modules
}