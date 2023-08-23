package antifraud.repositories;

import antifraud.models.BlackListedIP;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlackListedIPRepository extends CrudRepository<BlackListedIP, Long> {
    BlackListedIP findByIpAddress(String ipAddress);

    List<BlackListedIP> findAll();
}
