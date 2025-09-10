package lk.ijse.gdse72.backend.controller;

import lk.ijse.gdse72.backend.dto.VideoModuleDTO;
import lk.ijse.gdse72.backend.service.Impl.CloudinaryService;
import lk.ijse.gdse72.backend.service.VideoModuleService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/create")
    public VideoModuleDTO createModule(
            @RequestParam Long monthId,
            @RequestParam String title,
            @RequestParam(value = "video", required = false) MultipartFile[] videoFile
    ) throws IOException {
        // If a file is uploaded, send it to Cloudinary
        List<String> videoUrls = new ArrayList<>();

        if (videoFile != null) {
            for (MultipartFile file : videoFile) {
                if (!file.isEmpty()) {
                    String url = cloudinaryService.uploadVideo(file);
                    videoUrls.add(url);
                }
            }
        }

        VideoModuleDTO dto = new VideoModuleDTO();
        dto.setMonthId(monthId);
        dto.setTitle(title);
        dto.setVideoUrls(videoUrls);

        return videoModuleService.createModule(monthId, dto);
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

    @GetMapping("/month/{monthId}")
    public List<VideoModuleDTO> getModulesByMonth(@PathVariable Long monthId) {
        return videoModuleService.getModulesByMonth(monthId);
    }

    @GetMapping
    public List<VideoModuleDTO> getAllModules() {
        return videoModuleService.getAllModules();
    }
}