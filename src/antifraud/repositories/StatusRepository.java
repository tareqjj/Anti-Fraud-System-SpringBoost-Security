package antifraud.repositories;

import antifraud.models.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusRepository extends CrudRepository<Status, Long> {
    Status findByStatus(String status);

    List<Status> findAll();
}
