package sp26.se194638.ojt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sp26.se194638.ojt.model.entity.User;
import sp26.se194638.ojt.repository.UserRepository;

import java.util.Optional;

@Service

public class UserDetailImplementService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User account = userRepository.findByUsername(username);
        return (UserDetails) account;
    }
}
