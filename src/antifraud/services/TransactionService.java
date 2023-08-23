package antifraud.services;

import antifraud.DTO.NewFeedbackDTO;
import antifraud.DTO.NewTransactionDTO;
import antifraud.DTO.TransactionDTO;
import antifraud.DTO.TransactionResultDTO;
import antifraud.models.*;
import antifraud.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static antifraud.services.BlackListedCardService.luhnCheckValidator;
import static antifraud.services.BlackListedIPService.validateIP;

@Service
public class TransactionService {
    private final BlackListedIPRepository blackListedIPRepository;

    private final BlackListedCardRepository blackListedCardRepository;

    private final RegionRepository regionRepository;

    private final TransactionRepository transactionRepository;

    private final StatusRepository statusRepository;

    private final CardRepository cardRepository;

    public TransactionService(BlackListedIPRepository blackListedIPRepository,
                              BlackListedCardRepository blackListedCardRepository,
                              RegionRepository regionRepository, TransactionRepository transactionRepository,
                              StatusRepository statusRepository,
                              CardRepository cardRepository) {
        this.blackListedIPRepository = blackListedIPRepository;
        this.blackListedCardRepository = blackListedCardRepository;
        this.regionRepository = regionRepository;
        this.transactionRepository = transactionRepository;
        this.statusRepository = statusRepository;
        this.cardRepository = cardRepository;
    }

    private LocalDateTime validateDateTime(String date) {
        try {
            String localDateTimeString = date.replace('T', ' ');
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(localDateTimeString, formatter);
        } catch (DateTimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date time format must be yyyy-MM-ddTHH:mm:ss");
        }
    }
    private void saveTransactionResult(Transaction newTransaction, String result) {
        newTransaction.setResultStatus(statusRepository.findByStatus(result));
        transactionRepository.save(newTransaction);
    }

    private void increaseLimit(Optional<Transaction> optionalTransaction, String maxLimit) {
        Integer current_limit = 0;
        if (maxLimit.equals("maxAllowed"))
            current_limit = optionalTransaction.get().getCardNumber().getMaxAllowed();
        else
            current_limit = optionalTransaction.get().getCardNumber().getMaxManual();
        Long value_from_transaction = optionalTransaction.get().getAmount();
        Integer new_limit = (int) Math.ceil(0.8 * current_limit + 0.2 * value_from_transaction);
        Card cardNumber =optionalTransaction.get().getCardNumber();
        if (maxLimit.equals("maxAllowed"))
            cardNumber.setMaxAllowed(new_limit);
        else
            cardNumber.setMaxManual(new_limit);
        cardRepository.save(cardNumber);
    }

    private void decreaseLimit(Optional<Transaction> optionalTransaction, String maxLimit) {
        Integer current_limit = 0;
        if (maxLimit.equals("maxAllowed"))
            current_limit = optionalTransaction.get().getCardNumber().getMaxAllowed();
        else
            current_limit = optionalTransaction.get().getCardNumber().getMaxManual();
        Long value_from_transaction = optionalTransaction.get().getAmount();
        Integer new_limit = (int) Math.ceil(0.8 * current_limit - 0.2 * value_from_transaction);
        Card cardNumber =optionalTransaction.get().getCardNumber();
        if (maxLimit.equals("maxAllowed"))
            cardNumber.setMaxAllowed(new_limit);
        else
            cardNumber.setMaxManual(new_limit);
        cardRepository.save(cardNumber);
    }

    private TransactionDTO createTransactionDTO(Optional<Transaction> transaction){
        String status;
        if (transaction.get().getFeedbackStatus() == null)
            status = "";
        else
            status = transaction.get().getFeedbackStatus().getStatus();
        return new TransactionDTO(
                transaction.get().getId(),
                transaction.get().getAmount(),
                transaction.get().getIp(),
                transaction.get().getCardNumber().getNumber(),
                transaction.get().getTransactionRegion().getCode(),
                transaction.get().getDate(),
                transaction.get().getResultStatus().getStatus(),
                status);
    }


    public TransactionResultDTO validateTransaction(NewTransactionDTO newTransactionDTO) {
        LocalDateTime transactionDateTime = validateDateTime(newTransactionDTO.date());
        Region region = regionRepository.findByCode(newTransactionDTO.region());
        Card cardNumber = cardRepository.findByNumber(newTransactionDTO.number());
        if (!validateIP(newTransactionDTO.ip()) || !luhnCheckValidator(newTransactionDTO.number()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid IP or card number!");
        if (region == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid region!");
        if (cardNumber == null) {
            cardNumber = new Card(newTransactionDTO.number());
            cardRepository.save(cardNumber);
        }
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(newTransactionDTO.amount());
        newTransaction.setIp(newTransactionDTO.ip());
        newTransaction.setCardNumber(cardNumber);
        newTransaction.setDate(transactionDateTime);
        newTransaction.setTransactionRegion(region);
        transactionRepository.save(newTransaction);
        Long numberOfDistinctIP = transactionRepository.countDistinctIpByNumber(newTransactionDTO.number(),
                transactionDateTime.minusHours(1), transactionDateTime);
        Long numberOfDistinctRegion = transactionRepository.countDistinctTransactionRegionByNumber(newTransactionDTO.number(),
                transactionDateTime.minusHours(1), transactionDateTime);
        String info = "";
        String result = "";
        Integer maxAllowed = newTransaction.getCardNumber().getMaxAllowed();
        Integer maxManual = newTransaction.getCardNumber().getMaxManual();
        if (newTransactionDTO.amount() > maxManual) {
            info += "amount, ";
            result = "PROHIBITED";
        }
        if (blackListedCardRepository.findByCardNUmber(newTransactionDTO.number()) != null) {
            info += "card-number, ";
            result = "PROHIBITED";
        }
        if (blackListedIPRepository.findByIpAddress(newTransactionDTO.ip()) != null) {
            info += "ip, ";
            result = "PROHIBITED";
        }
        if (info.length() > 1 && numberOfDistinctIP < 3 && numberOfDistinctRegion < 3) {
            saveTransactionResult(newTransaction, result);
            return new TransactionResultDTO(result, info.substring(0, info.length() - 2));
        }
        if (newTransactionDTO.amount() > maxAllowed && newTransactionDTO.amount() <= maxManual) {
            info += "amount, ";
            result = "MANUAL_PROCESSING";
        }
        if (numberOfDistinctIP > 3) {
            info += "ip-correlation, ";
            result = "PROHIBITED";
        }
        if (numberOfDistinctRegion > 3) {
            info += "region-correlation, ";
            result = "PROHIBITED";
        }
        if (numberOfDistinctIP == 3 ) {
            info += "ip-correlation, ";
            result = "MANUAL_PROCESSING";
        }
        if (numberOfDistinctRegion == 3) {
            info += "region-correlation, ";
            result = "MANUAL_PROCESSING";
        }
        if (info.length() > 1) {
            saveTransactionResult(newTransaction, result);
            return new TransactionResultDTO(result, info.substring(0, info.length() - 2));
        }
        else {
            info = "none";
            result = "ALLOWED";
            saveTransactionResult(newTransaction, result);
            return new TransactionResultDTO(result, info);
        }
    }

    public TransactionDTO addFeedback(NewFeedbackDTO newFeedbackDTO) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(newFeedbackDTO.transactionId());
        Status feedbackStatus = statusRepository.findByStatus(newFeedbackDTO.feedback());
        if (optionalTransaction.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found!");
        if (feedbackStatus == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status!");
        if (optionalTransaction.get().getFeedbackStatus() != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Feedback exist!");
        if (optionalTransaction.get().getResultStatus().getStatus().equals(feedbackStatus.getStatus()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Feedback equals result status!");
        if (optionalTransaction.get().getResultStatus().getStatus().equals("ALLOWED") && feedbackStatus.getStatus().equals("MANUAL_PROCESSING"))
            decreaseLimit(optionalTransaction, "maxAllowed");
        else if (optionalTransaction.get().getResultStatus().getStatus().equals("ALLOWED") && feedbackStatus.getStatus().equals("PROHIBITED")) {
            decreaseLimit(optionalTransaction, "maxAllowed");
            decreaseLimit(optionalTransaction, "maxManual");
        }
        else if (optionalTransaction.get().getResultStatus().getStatus().equals("MANUAL_PROCESSING") && feedbackStatus.getStatus().equals("ALLOWED"))
            increaseLimit(optionalTransaction, "maxAllowed");
        else if (optionalTransaction.get().getResultStatus().getStatus().equals("MANUAL_PROCESSING") && feedbackStatus.getStatus().equals("PROHIBITED"))
            decreaseLimit(optionalTransaction, "maxManual");
        else if (optionalTransaction.get().getResultStatus().getStatus().equals("PROHIBITED") && feedbackStatus.getStatus().equals("ALLOWED")) {
            increaseLimit(optionalTransaction, "maxAllowed");
            increaseLimit(optionalTransaction, "maxManual");
        }
        else if (optionalTransaction.get().getResultStatus().getStatus().equals("PROHIBITED") && feedbackStatus.getStatus().equals("MANUAL_PROCESSING"))
            increaseLimit(optionalTransaction, "maxManual");
        optionalTransaction.get().setFeedbackStatus(feedbackStatus);
        transactionRepository.save(optionalTransaction.get());
        return createTransactionDTO(optionalTransaction);
    }

    public List<TransactionDTO> getTransactionHistory() {
        List<Transaction> transactionList = transactionRepository.findAll();
        if (transactionList.isEmpty())
            return List.of();
        List<TransactionDTO> transactionDTOList = new ArrayList<>();
        transactionList.forEach(transaction -> transactionDTOList.add(createTransactionDTO(Optional.ofNullable(transaction))));
        return transactionDTOList;
    }

    public List<TransactionDTO> getCardHistory(String cardNumber){
        Card cardHistory = cardRepository.findByNumber(cardNumber);
        if (!luhnCheckValidator(cardNumber))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid card number!");
        if (cardHistory == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found!");
        List<Transaction> transactionList = cardHistory.getTransactionList();
        if (transactionList.isEmpty())
            return List.of();
        List<TransactionDTO> transactionDTOList = new ArrayList<>();
        transactionList.forEach(transaction -> transactionDTOList.add(createTransactionDTO(Optional.ofNullable(transaction))));
        return transactionDTOList;
    }
}
