package antifraud.controllers;

import antifraud.DTO.*;
import antifraud.models.User;
import antifraud.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/auth/user")
    public ResponseEntity<UserDTO> signupUser(@Valid @RequestBody User user) {
        return userService.registerUser(user);
    }

    @GetMapping("/api/auth/list")
    public List<UserDTO> displayUserList() {
        return userService.getUserList();
    }

    @DeleteMapping("/api/auth/user/{username}")
    public DeletedUserDTO removeUser(@PathVariable String username) {
        return userService.deletedUser(username);
    }

    @PutMapping("/api/auth/role")
    public UserDTO updateUserRole(@Valid @RequestBody ChangeRoleDTO changeRoleDTO) {
        return userService.changeRole(changeRoleDTO);
    }

    @PutMapping("/api/auth/access")
    public StatusDTO updateUserLock(@Valid @RequestBody SetUserLockDTO setUserLockDTO) {
        return userService.setUserLock(setUserLockDTO);
    }
}
