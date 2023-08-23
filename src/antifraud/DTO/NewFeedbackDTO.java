package antifraud.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewFeedbackDTO(@NotNull Long transactionId, @NotBlank String feedback) {
}
