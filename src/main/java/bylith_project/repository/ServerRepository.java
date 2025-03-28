package bylith_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bylith_project.entity.Server;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {

}
