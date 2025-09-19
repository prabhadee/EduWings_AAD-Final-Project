package lk.ijse.gdse72.backend.controller;

import lk.ijse.gdse72.backend.dto.VideoModuleDTO;
import lk.ijse.gdse72.backend.entity.VideoModule;
import lk.ijse.gdse72.backend.repository.VideoModuleRepository;
import lk.ijse.gdse72.backend.service.Impl.CloudinaryService;
import lk.ijse.gdse72.backend.service.VideoModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@CrossOrigin("*")
public class VideoModuleController {

    private final VideoModuleService videoModuleService;
    private final CloudinaryService cloudinaryService;
    private final VideoModuleRepository videoModuleRepository;

//    @PostMapping("/create")
//    public VideoModuleDTO createModule(
//            @RequestParam Long monthId,
//            @RequestParam String title,
//            @RequestParam(value = "video", required = false) MultipartFile[] videoFile
//    ) throws IOException {
//        // If a file is uploaded, send it to Cloudinary
//        List<String> videoUrls = new ArrayList<>();
//
//        if (videoFile != null) {
//            for (MultipartFile file : videoFile) {
//                if (!file.isEmpty()) {
//                    String url = cloudinaryService.uploadVideo(file);
//                    videoUrls.add(url);
//                }
//            }
//        }
//
//        VideoModuleDTO dto = new VideoModuleDTO();
//        dto.setMonthId(monthId);
//        dto.setTitle(title);
//        dto.setVideoUrls(videoUrls);
//
//        return videoModuleService.createModule(monthId, dto);
//    }

    @PostMapping("/create")
    public VideoModuleDTO createModule(
            @RequestParam Long monthId,
            @RequestParam String title,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) throws IOException {
        List<String> videoUrls = new ArrayList<>();

        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String url = cloudinaryService.uploadFile(file); // auto-detect type
                    videoUrls.add(url);
                }
            }
        }

        VideoModuleDTO dto = new VideoModuleDTO();
        dto.setMonthId(monthId);
        dto.setTitle(title);
        dto.setVideoUrls(videoUrls); // maybe rename in DTO to fileUrls
        return videoModuleService.createModule(monthId, dto);
    }

//    @PutMapping(value = "/update-with-files/{moduleId}", consumes = "multipart/form-data")
//    public VideoModuleDTO updateModuleWithFiles(
//            @PathVariable Long moduleId,
//            @RequestParam(required = false) String title,
//            @RequestParam(required = false) Long monthId,
//            @RequestParam(value = "video", required = false) MultipartFile[] videoFile
//    ) throws IOException {
//        VideoModuleDTO existingModule = videoModuleService.getModuleById(moduleId);
//
//        if (title != null) existingModule.setTitle(title);
//        if (monthId != null) existingModule.setMonthId(monthId);
//
//        List<String> videoUrls = existingModule.getVideoUrls();
//        if (videoUrls == null) {
//            videoUrls = new ArrayList<>();
//        }
//
//        // Add new videos if any
//        if (videoFile != null) {
//            for (MultipartFile file : videoFile) {
//                if (!file.isEmpty()) {
//                    String url = cloudinaryService.uploadFile(file);
//                    videoUrls.add(url);
//                }
//            }
//        }
//
//        existingModule.setVideoUrls(videoUrls);
//        return videoModuleService.updateModule(moduleId, existingModule);
//    }

    @PutMapping(value = "/update-with-files/{moduleId}", consumes = "multipart/form-data")
    public VideoModuleDTO updateModuleWithFiles(
            @PathVariable Long moduleId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long monthId,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) throws IOException {
        VideoModuleDTO existingModule = videoModuleService.getModuleById(moduleId);

        if (title != null) existingModule.setTitle(title);
        if (monthId != null) existingModule.setMonthId(monthId);

        List<String> videoUrls = existingModule.getVideoUrls(); // ⚡ consider renaming in DTO
        if (videoUrls == null) {
            videoUrls = new ArrayList<>();
        }

        // Add new files (images, videos, pdfs, etc.)
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String url = cloudinaryService.uploadFile(file); // auto-detects type
                    videoUrls.add(url);
                }
            }
        }

        existingModule.setVideoUrls(videoUrls); // ⚡ rename to setFileUrls if you generalize
        return videoModuleService.updateModule(moduleId, existingModule);
    }

    @PutMapping("/update/{moduleId}")
    public VideoModuleDTO updateModule(@PathVariable Long moduleId, @RequestBody VideoModuleDTO dto) {
        return videoModuleService.updateModule(moduleId, dto);
    }

    @DeleteMapping("/delete/{moduleId}")
    public String deleteModule(@PathVariable Long moduleId) {
        videoModuleService.deleteModule(moduleId);
        return "Module deleted successfully!";
    }

    @GetMapping("/{moduleId}")
    public VideoModuleDTO getModuleById(@PathVariable Long moduleId) {
        return videoModuleService.getModuleById(moduleId);
    }

    @GetMapping("/by-month/{monthId}")
    public List<VideoModuleDTO> getModulesByMonth(@PathVariable Long monthId) {
        return videoModuleService.getModulesByMonth(monthId);
    }

    @GetMapping
    public List<VideoModuleDTO> getAllModules() {
        return videoModuleService.getAllModules();
    }

    @PostMapping("/by-months")
    public ResponseEntity<List<VideoModuleDTO>> getModulesByMonthIds(@RequestBody List<Long> monthIds) {
        return ResponseEntity.ok(videoModuleService.getModulesByMonthIds(monthIds));
    }

    @DeleteMapping("/{moduleId}/videos/{index}")
    public VideoModuleDTO deleteVideoFromModule(@PathVariable Long moduleId, @PathVariable int index) {
        VideoModuleDTO module = videoModuleService.getModuleById(moduleId);
        List<String> videoUrls = module.getVideoUrls();

        if (index >= 0 && index < videoUrls.size()) {
            String urlToDelete = videoUrls.get(index);
            // Optional: Delete from Cloudinary
            // cloudinaryService.deleteVideo(urlToDelete);

            videoUrls.remove(index);
            module.setVideoUrls(videoUrls);
            return videoModuleService.updateModule(moduleId, module);
        }

        throw new RuntimeException("Invalid video index");
    }

    @GetMapping("/videos/month/{monthId}")
    public List<VideoModule> getVideosByMonth(@PathVariable Long monthId) {
        return videoModuleRepository.findByMonth_MonthId(monthId);
    }
}