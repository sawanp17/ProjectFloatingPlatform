package com.iitj.projectplatform;

import com.iitj.projectplatform.Repositories.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(
                ">>  loadUserByName called for {" + username + "}"
        );
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("The user with this username doesn't exist"));

        PasswordEncoder pe = SecurityConfig.passwordEncoder();
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if (user.getRole().equals(Role.Professor)) {
            System.out.println(">>  Assigning role of Professor to " + user.getUsername());
            authorities.add(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
        }
        else if (user.getRole().equals(Role.Student)){
            System.out.println(">>  Assigning role of Student to " + user.getUsername());

            authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
        }

//        System.out.println(user.getUsername());
//        System.out.println(user.getPassword());
//        System.out.println(pe.encode(user.getPassword()));
        return  org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(pe.encode(user.getPassword()))
                .authorities(authorities)
                .build();

    }
}
