package lk.ijse.gdse72.backend.repository;

import lk.ijse.gdse72.backend.entity.VideoModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoModuleRepository extends JpaRepository<VideoModule, Long> {

    // Custom query to find modules by month ID
    @Query("SELECT m FROM VideoModule m WHERE m.month.monthId = :monthId")
    List<VideoModule> findByMonthId(@Param("monthId") Long monthId);

    // Custom query to find modules by multiple month IDs
    @Query("SELECT m FROM VideoModule m WHERE m.month.monthId IN :monthIds")
    List<VideoModule> findByMonthIds(@Param("monthIds") List<Long> monthIds);
}