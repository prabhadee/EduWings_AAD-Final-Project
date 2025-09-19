//package lk.ijse.gdse72.backend.service.Impl;
//
//import lk.ijse.gdse72.backend.dto.VideoModuleDTO;
//import lk.ijse.gdse72.backend.entity.BatchMonth;
//import lk.ijse.gdse72.backend.entity.VideoModule;
//import lk.ijse.gdse72.backend.repository.BatchMonthRepository;
//import lk.ijse.gdse72.backend.repository.VideoModuleRepository;
//import lk.ijse.gdse72.backend.service.VideoModuleService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class VideoModuleServiceImpl implements VideoModuleService {
//
//    private final VideoModuleRepository moduleRepository;
//    private final BatchMonthRepository monthRepository;
//
//    @Override
//    public VideoModuleDTO createModule(Long monthId, VideoModuleDTO dto) {
//        BatchMonth month = monthRepository.findById(monthId)
//                .orElseThrow(() -> new RuntimeException("Month not found"));
//
//        VideoModule module = VideoModule.builder()
//                .title(dto.getTitle())
//                .month(month)
//                .build();
//
//        // Set video URLs from DTO
//        module.setVideoUrlsList(dto.getVideoUrls());
//
//        moduleRepository.save(module);
//        dto.setModuleId(module.getModuleId());
//        return dto;
//    }
//
//    @Override
//    public VideoModuleDTO updateModule(Long moduleId, VideoModuleDTO dto) {
//        VideoModule module = moduleRepository.findById(moduleId)
//                .orElseThrow(() -> new RuntimeException("Module not found"));
//
//        module.setTitle(dto.getTitle());
//        // Update video URLs from DTO
//        module.setVideoUrlsList(dto.getVideoUrls());
//
//        moduleRepository.save(module);
//
//        // Return updated DTO
//        return VideoModuleDTO.builder()
//                .moduleId(module.getModuleId())
//                .title(module.getTitle())
//                .videoUrls(module.getVideoUrlsList())
//                .monthId(module.getMonth().getMonthId())
//                .build();
//    }
//
//    @Override
//    public void deleteModule(Long moduleId) {
//        moduleRepository.deleteById(moduleId);
//    }
//
//    @Override
//    public VideoModuleDTO getModuleById(Long moduleId) {
//        VideoModule module = moduleRepository.findById(moduleId)
//                .orElseThrow(() -> new RuntimeException("Module not found"));
//
//        return VideoModuleDTO.builder()
//                .moduleId(module.getModuleId())
//                .title(module.getTitle())
//                .videoUrls(module.getVideoUrlsList())
//                .monthId(module.getMonth().getMonthId())
//                .build();
//    }
//
//    @Override
//    public List<VideoModuleDTO> getModulesByMonth(Long monthId) {
//        List<VideoModule> modules = moduleRepository.findByMonthId(monthId);
//
//        return modules.stream()
//                .map(m -> VideoModuleDTO.builder()
//                        .moduleId(m.getModuleId())
//                        .title(m.getTitle())
//                        .videoUrls(m.getVideoUrlsList())
//                        .monthId(m.getMonth().getMonthId())
//                        .build()
//                ).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<VideoModuleDTO> getAllModules() {
//        List<VideoModule> modules = moduleRepository.findAll();
//
//        return modules.stream()
//                .map(m -> VideoModuleDTO.builder()
//                        .moduleId(m.getModuleId())
//                        .title(m.getTitle())
//                        .videoUrls(m.getVideoUrlsList())
//                        .monthId(m.getMonth().getMonthId())
//                        .build()
//                ).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<VideoModuleDTO> getModulesByMonthIds(List<Long> monthIds) {
//        if (monthIds == null || monthIds.isEmpty()) {
//            return new ArrayList<>();
//        }
//        List<VideoModule> modules = moduleRepository.findByMonthIds(monthIds);
//        return modules.stream()
//                .map(m -> VideoModuleDTO.builder()
//                        .moduleId(m.getModuleId())
//                        .title(m.getTitle())
//                        .videoUrls(m.getVideoUrlsList())
//                        .monthId(m.getMonth().getMonthId())
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//}

package lk.ijse.gdse72.backend.service.Impl;

import lk.ijse.gdse72.backend.dto.VideoModuleDTO;
import lk.ijse.gdse72.backend.entity.BatchMonth;
import lk.ijse.gdse72.backend.entity.VideoModule;
import lk.ijse.gdse72.backend.repository.BatchMonthRepository;
import lk.ijse.gdse72.backend.repository.VideoModuleRepository;
import lk.ijse.gdse72.backend.service.VideoModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoModuleServiceImpl implements VideoModuleService {

    private final VideoModuleRepository moduleRepository;
    private final BatchMonthRepository monthRepository;

    @Override
    @Transactional
    public VideoModuleDTO createModule(Long monthId, VideoModuleDTO dto) {
        BatchMonth month = monthRepository.findById(monthId)
                .orElseThrow(() -> new RuntimeException("Month not found with ID: " + monthId));

        // Ensure video URLs list is not null
        List<String> videoUrls = dto.getVideoUrls() != null ? dto.getVideoUrls() : new ArrayList<>();

        VideoModule module = VideoModule.builder()
                .title(dto.getTitle())
                .month(month)
                .build();

        // Set video URLs from DTO
        module.setVideoUrlsList(videoUrls);

        // Save and flush to ensure immediate persistence
        VideoModule savedModule = moduleRepository.saveAndFlush(module);

        // Verify the save was successful
        System.out.println("Module saved with ID: " + savedModule.getModuleId());
        System.out.println("Video URLs saved: " + savedModule.getVideoUrlsList());

        // Return DTO with saved module ID
        return VideoModuleDTO.builder()
                .moduleId(savedModule.getModuleId())
                .title(savedModule.getTitle())
                .videoUrls(savedModule.getVideoUrlsList())
                .monthId(savedModule.getMonth().getMonthId())
                .build();
    }

    @Override
    @Transactional
    public VideoModuleDTO updateModule(Long moduleId, VideoModuleDTO dto) {
        VideoModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found with ID: " + moduleId));

        // Update fields
        if (dto.getTitle() != null) {
            module.setTitle(dto.getTitle());
        }

        // Update video URLs - ensure list is not null
        List<String> videoUrls = dto.getVideoUrls() != null ? dto.getVideoUrls() : new ArrayList<>();
        module.setVideoUrlsList(videoUrls);

        // Save and flush
        VideoModule updatedModule = moduleRepository.saveAndFlush(module);

        // Verify the update
        System.out.println("Module updated with ID: " + updatedModule.getModuleId());
        System.out.println("Updated video URLs: " + updatedModule.getVideoUrlsList());

        // Return updated DTO
        return VideoModuleDTO.builder()
                .moduleId(updatedModule.getModuleId())
                .title(updatedModule.getTitle())
                .videoUrls(updatedModule.getVideoUrlsList())
                .monthId(updatedModule.getMonth().getMonthId())
                .build();
    }

    @Override
    @Transactional
    public void deleteModule(Long moduleId) {
        if (!moduleRepository.existsById(moduleId)) {
            throw new RuntimeException("Module not found with ID: " + moduleId);
        }
        moduleRepository.deleteById(moduleId);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoModuleDTO getModuleById(Long moduleId) {
        VideoModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found with ID: " + moduleId));

        return VideoModuleDTO.builder()
                .moduleId(module.getModuleId())
                .title(module.getTitle())
                .videoUrls(module.getVideoUrlsList())
                .monthId(module.getMonth().getMonthId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoModuleDTO> getModulesByMonth(Long monthId) {
        List<VideoModule> modules = moduleRepository.findByMonthId(monthId);

        return modules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoModuleDTO> getAllModules() {
        List<VideoModule> modules = moduleRepository.findAll();

        return modules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoModuleDTO> getModulesByMonthIds(List<Long> monthIds) {
        if (monthIds == null || monthIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<VideoModule> modules = moduleRepository.findByMonthIds(monthIds);
        return modules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert entity to DTO
    private VideoModuleDTO convertToDTO(VideoModule module) {
        return VideoModuleDTO.builder()
                .moduleId(module.getModuleId())
                .title(module.getTitle())
                .videoUrls(module.getVideoUrlsList())
                .monthId(module.getMonth().getMonthId())
                .build();
    }
}
