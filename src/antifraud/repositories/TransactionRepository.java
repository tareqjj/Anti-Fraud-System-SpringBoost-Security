package antifraud.repositories;

import antifraud.models.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findAll();

    @Query("select count (distinct t.transactionRegion) from Transaction t where t.cardNumber.number = :cardNumber " +
            "and t.date between :startDate and :endDate")
    Long countDistinctTransactionRegionByNumber(
            @Param("cardNumber") String cardNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("select count (distinct t.ip) from Transaction t where t.cardNumber.number = :cardNumber " +
            "and t.date between :startDate and :endDate")
    Long countDistinctIpByNumber(
            @Param("cardNumber") String cardNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
