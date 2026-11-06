package in.malabartrails;
import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.data.jpa.repository.Query;import java.util.*;
public interface EnquiryRepository extends JpaRepository<Enquiry,UUID>{List<Enquiry> findTop10ByOrderByCreatedAtDesc();long countByStatus(String status);Optional<Enquiry> findByIdempotencyKey(String key);@Query("select e.status,count(e) from Enquiry e group by e.status")List<Object[]> countGroupedByStatus();}
