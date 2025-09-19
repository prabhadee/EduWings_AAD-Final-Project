package lk.ijse.gdse72.backend.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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

    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        try {
            // Auto-detect file type and use secure URL
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "folder", "video_modules", // organize uploads in folder
                            "use_filename", true,
                            "unique_filename", true
                    )
            );

            String secureUrl = uploadResult.get("secure_url").toString();
            System.out.println("File uploaded successfully: " + secureUrl);
            return secureUrl;

        } catch (IOException e) {
            System.err.println("Error uploading file to Cloudinary: " + e.getMessage());
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            System.err.println("Error deleting file from Cloudinary: " + e.getMessage());
        }
    }
}