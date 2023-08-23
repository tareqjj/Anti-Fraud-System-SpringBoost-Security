package antifraud.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.LuhnCheck;

import java.util.List;

@Entity
@Table(name = "card_numbers")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    @LuhnCheck(message = "Must enter a valid card number!")
    private String number;

    @Column(columnDefinition = "integer default 200")
    private Integer maxAllowed;

    @Column(columnDefinition = "integer default 1500")
    private Integer maxManual;

    @OneToMany(mappedBy = "cardNumber")
    private List<Transaction> transactionList;

    public Card() {
    }

    public Card(String number) {
        this.number = number;
        this.maxAllowed = 200;
        this.maxManual = 1500;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getMaxAllowed() {
        return maxAllowed;
    }

    public void setMaxAllowed(Integer maxAllowed) {
        this.maxAllowed = maxAllowed;
    }

    public Integer getMaxManual() {
        return maxManual;
    }

    public void setMaxManual(Integer maxManual) {
        this.maxManual = maxManual;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }
}
