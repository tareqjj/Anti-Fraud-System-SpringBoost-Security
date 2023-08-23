package antifraud.DTO;

public record DeletedUserDTO(String username, String status) {
    public DeletedUserDTO(String username) {
        this(username, "Deleted successfully!");
    }
}
