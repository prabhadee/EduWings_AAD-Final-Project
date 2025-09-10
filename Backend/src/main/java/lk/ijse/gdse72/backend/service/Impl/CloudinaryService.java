package lk.ijse.gdse72.backend.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dqt3ec7fm",
                "api_key", "868614223567543",
                "api_secret", "SwxHavUgDGlqv1zm8MYqa1oXhzk"
        ));
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString();
    }


//    public String uploadVideo(MultipartFile file) {
//        try {
//            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
//                    ObjectUtils.asMap("resource_type", "video"));
//            return uploadResult.get("secure_url").toString();
//        } catch (IOException e) {
//            throw new RuntimeException("Video upload failed", e);
//        }
//    }
}
