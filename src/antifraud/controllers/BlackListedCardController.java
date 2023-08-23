package antifraud.controllers;

import antifraud.DTO.StatusDTO;
import antifraud.models.BlackListedCard;
import antifraud.services.BlackListedCardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BlackListedCardController {
    private final BlackListedCardService blackListedCardService;

    public BlackListedCardController(BlackListedCardService blackListedCardService) {
        this.blackListedCardService = blackListedCardService;
    }

    @PostMapping("/api/antifraud/stolencard")
    public ResponseEntity<BlackListedCard> updateStolenCard(@Valid @RequestBody BlackListedCard blackListedCard) {
        return blackListedCardService.addStolenCard(blackListedCard);
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public StatusDTO removeCard(@PathVariable String number){
        return blackListedCardService.deleteCardNumber(number);
    }

    @GetMapping("/api/antifraud/stolencard")
    public List<BlackListedCard> displayCardList(){
        return blackListedCardService.getCardNumberList();
    }
}
