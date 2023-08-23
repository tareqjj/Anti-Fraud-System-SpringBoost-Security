package antifraud.services;

import antifraud.DTO.*;
import antifraud.models.User;
import antifraud.repositories.RoleRepository;
import antifraud.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserDTO createUserDTO(User user) {
        String roleNoPrefix = user.getUserRole().getRole().replace("ROLE_", "");
        return new UserDTO(user.getId(), user.getName(), user.getUsername(), roleNoPrefix) ;
    }

    public ResponseEntity<UserDTO> registerUser(User user) {
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()) != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User exist!");
        if (userRepository.findAll().isEmpty()) {
            user.setUserRole(roleRepository.findByRole("ROLE_ADMINISTRATOR"));
            user.setAccountNonLocked(true);
        }
        else
            user.setUserRole(roleRepository.findByRole("ROLE_MERCHANT"));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new ResponseEntity<>(createUserDTO(user), HttpStatus.CREATED);
    }

    public List<UserDTO> getUserList() {
        List<User> userList = userRepository.findAll();
        if (userList.isEmpty())
            return List.of();
        List<UserDTO> userDTOList = new ArrayList<>();
        userList.forEach(user -> userDTOList.add(createUserDTO(user)));
        return userDTOList;
    }

    public DeletedUserDTO deletedUser(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        userRepository.delete(user);
        return new DeletedUserDTO(username);
    }

    public UserDTO changeRole(ChangeRoleDTO changeRoleDTO) {
        String rolePrefix = "ROLE_" + changeRoleDTO.role();
        User user = userRepository.findByUsernameIgnoreCase(changeRoleDTO.username());
        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        else if (user.getUserRole().getRole().equals(rolePrefix))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User role exist!");
        else if (rolePrefix.equals("ROLE_MERCHANT") || rolePrefix.equals("ROLE_SUPPORT")) {
            user.setUserRole(roleRepository.findByRole(rolePrefix));
            userRepository.save(user);
            return createUserDTO(user);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is not valid!");
    }

    public StatusDTO setUserLock(SetUserLockDTO setUserLockDTO) {
        User user = userRepository.findByUsernameIgnoreCase(setUserLockDTO.username());
        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        else if (user.getUserRole().getRole().equals("ROLE_ADMINISTRATOR"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock ADMINISTRATOR!");
        else if (setUserLockDTO.operation().equals("LOCK")) {
            user.setAccountNonLocked(false);
            userRepository.save(user);
            return new StatusDTO(String.format("User %s locked!", user.getUsername()));
        }
        else if (setUserLockDTO.operation().equals("UNLOCK")) {
            user.setAccountNonLocked(true);
            userRepository.save(user);
            return new StatusDTO(String.format("User %s unlocked!", user.getUsername()));
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Operation!");
    }
}
