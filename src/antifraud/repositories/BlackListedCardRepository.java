package antifraud.repositories;

import antifraud.models.BlackListedCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlackListedCardRepository extends CrudRepository<BlackListedCard, Long> {
    BlackListedCard findByCardNUmber(String cardNumber);

    List<BlackListedCard> findAll();
}
