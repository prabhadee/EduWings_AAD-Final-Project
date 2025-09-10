package lk.ijse.gdse72.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "video_module")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moduleId;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "month_id", nullable = false)
    private BatchMonth month;

    // For storing multiple video URLs as JSON
    @Column(columnDefinition = "JSON")
    private String videoUrls;

    // Helper method to get video URLs as list
    public List<String> getVideoUrlsList() {
        if (videoUrls == null || videoUrls.isEmpty() || videoUrls.equals("[]")) {
            return new ArrayList<>();
        }
        try {
            // Simple parsing for JSON array - in real implementation use a JSON library
            String cleanJson = videoUrls.replace("[", "").replace("]", "").replace("\"", "");
            if (cleanJson.isEmpty()) {
                return new ArrayList<>();
            }
            String[] urls = cleanJson.split(",");
            List<String> urlList = new ArrayList<>();
            for (String url : urls) {
                urlList.add(url.trim());
            }
            return urlList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Helper method to set video URLs from list
    public void setVideoUrlsList(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            this.videoUrls = "[]";
            return;
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < urls.size(); i++) {
            sb.append("\"").append(urls.get(i)).append("\"");
            if (i < urls.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        this.videoUrls = sb.toString();
    }
}