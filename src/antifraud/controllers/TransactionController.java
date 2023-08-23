package antifraud.controllers;

import antifraud.DTO.*;
import antifraud.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/api/antifraud/transaction")
    public TransactionResultDTO transactionValidation(@Valid @RequestBody NewTransactionDTO newTransactionDTO) {
        return transactionService.validateTransaction(newTransactionDTO);
    }

    @PutMapping("/api/antifraud/transaction")
    public TransactionDTO updateFeedback(@Valid @RequestBody NewFeedbackDTO newFeedbackDTO){
        return transactionService.addFeedback(newFeedbackDTO);
    }

    @GetMapping("/api/antifraud/history")
    public List<TransactionDTO> displayTransactionHistory() {
        return transactionService.getTransactionHistory();
    }

    @GetMapping("/api/antifraud/history/{cardNumber}")
        public List<TransactionDTO> displayCardHistory(@PathVariable String cardNumber) {
        return transactionService.getCardHistory(cardNumber);
        }
}
