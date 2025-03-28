package bylith_project.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bylith_project.entity.RequestHistory;
import bylith_project.entity.Server;

@Repository
public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long> {

	List<RequestHistory> findTop5ByServerOrderByTimestampDesc(Server server);

	List<RequestHistory> findByServerOrderByTimestampDesc(Server server);

	List<RequestHistory> findTop10ByServerOrderByTimestampDesc(Server server);

	List<RequestHistory> findByServerAndTimestampBeforeOrderByTimestampDesc(Server server, LocalDateTime time);
}
