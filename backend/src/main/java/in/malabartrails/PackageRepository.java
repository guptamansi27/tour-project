package in.malabartrails;
import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface PackageRepository extends JpaRepository<PackageEntity,UUID>{List<PackageEntity> findByStatusOrderByTitleAsc(String status);Optional<PackageEntity> findBySlugAndStatus(String slug,String status);long countByStatus(String status);List<PackageEntity> findByStatusAndPublishAtLessThanEqual(String status,java.time.OffsetDateTime instant);}
