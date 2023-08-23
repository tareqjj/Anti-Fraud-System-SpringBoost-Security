package antifraud.services;

import antifraud.DTO.StatusDTO;
import antifraud.models.BlackListedCard;
import antifraud.repositories.BlackListedCardRepository;
import org.hibernate.validator.internal.constraintvalidators.hv.LuhnCheckValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlackListedCardService {
    private final BlackListedCardRepository blackListedCardRepository;

    public BlackListedCardService(BlackListedCardRepository blackListedCardRepository) {
        this.blackListedCardRepository = blackListedCardRepository;
    }

    public static boolean luhnCheckValidator(String cardNumber){
        List<Integer> cardIntegerList = new ArrayList<>();
        String[] cardNumberArray = cardNumber.split("");
        for (String number:cardNumberArray)
            cardIntegerList.add(Integer.parseInt(number));
        Integer checkDigit = cardIntegerList.remove(cardIntegerList.size()-1);
        LuhnCheckValidator luhnCheckValidator = new LuhnCheckValidator();
        return luhnCheckValidator.isCheckDigitValid(cardIntegerList, Character.forDigit(checkDigit, 10));
    }

    public ResponseEntity<BlackListedCard> addStolenCard(BlackListedCard blackListedCard) {
        if (blackListedCardRepository.findByCardNUmber(blackListedCard.getCardNUmber()) != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Card Number exist!");
        blackListedCardRepository.save(blackListedCard); //validation on card number is handled at model
        return new ResponseEntity<>(blackListedCard, HttpStatus.OK);
    }

    public StatusDTO deleteCardNumber(String cardNumber){
        if (!luhnCheckValidator(cardNumber))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid card number!");
        BlackListedCard blackListedCard = blackListedCardRepository.findByCardNUmber(cardNumber);
        if (blackListedCard == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card number not found!");
        blackListedCardRepository.delete(blackListedCard);
        return new StatusDTO(String.format("Card %s successfully removed!",  cardNumber));
    }

    public List<BlackListedCard> getCardNumberList() {
        List<BlackListedCard> blackListedCardList = blackListedCardRepository.findAll();
        if (blackListedCardList.isEmpty())
            return List.of();
        return blackListedCardList;
    }
}
