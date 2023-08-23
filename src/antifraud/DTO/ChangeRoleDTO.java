package antifraud.DTO;

import jakarta.validation.constraints.NotBlank;

// Must use validation annotation in record class for @Valid to work
public record ChangeRoleDTO(@NotBlank String username, @NotBlank String role) {
}
