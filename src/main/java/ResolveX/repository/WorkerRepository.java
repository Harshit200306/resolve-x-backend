package ResolveX.repository;

import ResolveX.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    List<Worker> findByAvailableTrue();
    Optional<Worker> findByUserEmail(String email);
}