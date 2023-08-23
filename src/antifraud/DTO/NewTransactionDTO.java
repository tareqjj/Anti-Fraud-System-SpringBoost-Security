package antifraud.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewTransactionDTO(@NotNull @Min(value = 1, message = "Transaction must be greater than 0!") Long amount,
                                @NotBlank String ip, @NotBlank String number, @NotBlank String region,
                                @NotBlank String date) {
}
