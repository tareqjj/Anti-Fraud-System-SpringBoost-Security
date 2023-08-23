package antifraud.repositories;

import antifraud.models.Region;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends CrudRepository<Region, Long> {
    Region findByCode(String code);

    List<Region> findAll();
}
