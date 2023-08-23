package antifraud.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
@Table(name = "transaction_status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String status;

    @OneToMany(mappedBy = "resultStatus")
    private List<Transaction> transactionResultList;

    @OneToMany(mappedBy = "feedbackStatus")
    private List<Transaction> transactionFeedbackList;

    public Status() {
    }

    public Status(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Transaction> getTransactionResultList() {
        return transactionResultList;
    }

    public void setTransactionResultList(List<Transaction> transactionResultList) {
        this.transactionResultList = transactionResultList;
    }

    public List<Transaction> getTransactionFeedbackList() {
        return transactionFeedbackList;
    }

    public void setTransactionFeedbackList(List<Transaction> transactionFeedbackList) {
        this.transactionFeedbackList = transactionFeedbackList;
    }
}
