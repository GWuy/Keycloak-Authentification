package sp26.se194638.ojt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sp26.se194638.ojt.model.entity.User;
import sp26.se194638.ojt.repository.UserRepository;

@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public CustomUserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    return new CustomUserDetails(user);
  }
}
