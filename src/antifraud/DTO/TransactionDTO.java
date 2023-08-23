package antifraud.DTO;

import java.time.LocalDateTime;

public record TransactionDTO(Long transactionId, Long amount, String ip, String number, String region,
                             LocalDateTime date, String result, String feedback) {
}
