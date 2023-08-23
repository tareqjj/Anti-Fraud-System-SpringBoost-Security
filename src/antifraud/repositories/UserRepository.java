package antifraud.repositories;

import antifraud.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsernameIgnoreCase(String username);

    List<User> findAll();
}
