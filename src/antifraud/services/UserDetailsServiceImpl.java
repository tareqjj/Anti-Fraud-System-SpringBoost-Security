package antifraud.services;

import antifraud.models.User;
import antifraud.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null) {
            throw new UsernameNotFoundException("Not found: " + username);
        }
        //Convert String roles to GrantedAuthority type list
        List<GrantedAuthority> grantedAuthorityList = List.of(new SimpleGrantedAuthority(user.getUserRole().getRole()));
        // Uses default implementation of UserDetails, true values corresponds with enables, accountNonExpired, and credentialsNonExpired
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                user.isAccountNonLocked(),
                grantedAuthorityList
        );
    }
}
