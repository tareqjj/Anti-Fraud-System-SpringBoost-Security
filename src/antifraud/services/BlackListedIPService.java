package antifraud.services;

import antifraud.DTO.StatusDTO;
import antifraud.models.BlackListedIP;
import antifraud.repositories.BlackListedIPRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BlackListedIPService {
    private final BlackListedIPRepository blackListedIPRepository;

    public BlackListedIPService(BlackListedIPRepository blackListedIPRepository) {
        this.blackListedIPRepository = blackListedIPRepository;
    }

    public static boolean validateIP(String ipAddress) {
        String regex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
        return ipAddress.matches(regex);
    }

    public ResponseEntity<BlackListedIP> addSuspiciousIPAddress(BlackListedIP blackListedIP) {
        if (blackListedIPRepository.findByIpAddress(blackListedIP.getIpAddress()) != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "IP Address exist!");
        blackListedIPRepository.save(blackListedIP);  //validation on IP address is handled at model
        return new ResponseEntity<>(blackListedIP, HttpStatus.OK);
    }

    public StatusDTO deleteIPAddress(String ipAddress){
        if (!validateIP(ipAddress))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid IP Address!");
        BlackListedIP blackListedIP = blackListedIPRepository.findByIpAddress(ipAddress);
        if (blackListedIP == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "IP Address not found!");
        blackListedIPRepository.delete(blackListedIP);
        return new StatusDTO(String.format("IP %s successfully removed!",  ipAddress));
    }

    public List<BlackListedIP> getIPAddressList() {
        List<BlackListedIP> blackListedIPList = blackListedIPRepository.findAll();
        if (blackListedIPList.isEmpty())
            return List.of();
        return blackListedIPList;
    }
}
