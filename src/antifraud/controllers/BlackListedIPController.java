package antifraud.controllers;

import antifraud.DTO.StatusDTO;
import antifraud.models.BlackListedIP;
import antifraud.services.BlackListedIPService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BlackListedIPController {
    private final BlackListedIPService blackListedIPService;

    public BlackListedIPController(BlackListedIPService blackListedIPService) {
        this.blackListedIPService = blackListedIPService;
    }

    @PostMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<BlackListedIP> updateSuspiciousIP(@Valid @RequestBody BlackListedIP blackListedIP) {
        return blackListedIPService.addSuspiciousIPAddress(blackListedIP);
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public StatusDTO removeIPAddress(@PathVariable String ip){
        return blackListedIPService.deleteIPAddress(ip);
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    public List<BlackListedIP> displayIPList(){
        return blackListedIPService.getIPAddressList();
    }
}
