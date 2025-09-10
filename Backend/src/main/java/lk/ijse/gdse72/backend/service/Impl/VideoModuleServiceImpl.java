package lk.ijse.gdse72.backend.service.Impl;

import lk.ijse.gdse72.backend.dto.VideoModuleDTO;
import lk.ijse.gdse72.backend.entity.BatchMonth;
import lk.ijse.gdse72.backend.entity.VideoModule;
import lk.ijse.gdse72.backend.repository.BatchMonthRepository;
import lk.ijse.gdse72.backend.repository.VideoModuleRepository;
import lk.ijse.gdse72.backend.service.VideoModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoModuleServiceImpl implements VideoModuleService {

    private final VideoModuleRepository moduleRepository;
    private final BatchMonthRepository monthRepository;

    @Override
    public VideoModuleDTO createModule(Long monthId, VideoModuleDTO dto) {
        BatchMonth month = monthRepository.findById(monthId)
                .orElseThrow(() -> new RuntimeException("Month not found"));

        VideoModule module = VideoModule.builder()
                .title(dto.getTitle())
                .month(month)
                .build();

        // Set video URLs from DTO
        module.setVideoUrlsList(dto.getVideoUrls());

        moduleRepository.save(module);
        dto.setModuleId(module.getModuleId());
        return dto;
    }

    @Override
    public VideoModuleDTO updateModule(Long moduleId, VideoModuleDTO dto) {
        VideoModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        module.setTitle(dto.getTitle());
        // Update video URLs from DTO
        module.setVideoUrlsList(dto.getVideoUrls());

        moduleRepository.save(module);

        // Return updated DTO
        return VideoModuleDTO.builder()
                .moduleId(module.getModuleId())
                .title(module.getTitle())
                .videoUrls(module.getVideoUrlsList())
                .monthId(module.getMonth().getMonthId())
                .build();
    }

    @Override
    public void deleteModule(Long moduleId) {
        moduleRepository.deleteById(moduleId);
    }

    @Override
    public VideoModuleDTO getModuleById(Long moduleId) {
        VideoModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        return VideoModuleDTO.builder()
                .moduleId(module.getModuleId())
                .title(module.getTitle())
                .videoUrls(module.getVideoUrlsList())
                .monthId(module.getMonth().getMonthId())
                .build();
    }

    @Override
    public List<VideoModuleDTO> getModulesByMonth(Long monthId) {
        List<VideoModule> modules = moduleRepository.findByMonthId(monthId);

        return modules.stream()
                .map(m -> VideoModuleDTO.builder()
                        .moduleId(m.getModuleId())
                        .title(m.getTitle())
                        .videoUrls(m.getVideoUrlsList())
                        .monthId(m.getMonth().getMonthId())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public List<VideoModuleDTO> getAllModules() {
        List<VideoModule> modules = moduleRepository.findAll();

        return modules.stream()
                .map(m -> VideoModuleDTO.builder()
                        .moduleId(m.getModuleId())
                        .title(m.getTitle())
                        .videoUrls(m.getVideoUrlsList())
                        .monthId(m.getMonth().getMonthId())
                        .build()
                ).collect(Collectors.toList());
    }
}