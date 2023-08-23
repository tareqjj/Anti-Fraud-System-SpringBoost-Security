package antifraud.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Transaction must be greater than 0!")
    private Long amount;

    @NotBlank
    @Pattern(regexp = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$", message = "Invalid IP Address!")
    private String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card cardNumber;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region transactionRegion;

    @ManyToOne
    @JoinColumn(name = "result_status_id")
    private Status resultStatus;

    @ManyToOne
    @JoinColumn(name = "feedback_status_id")
    private Status feedbackStatus;

    public Transaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Card getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(Card cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Region getTransactionRegion() {
        return transactionRegion;
    }

    public void setTransactionRegion(Region transactionRegion) {
        this.transactionRegion = transactionRegion;
    }

    public Status getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(Status resultStatus) {
        this.resultStatus = resultStatus;
    }

    public Status getFeedbackStatus() {
        return feedbackStatus;
    }

    public void setFeedbackStatus(Status feedbackStatus) {
        this.feedbackStatus = feedbackStatus;
    }
}
