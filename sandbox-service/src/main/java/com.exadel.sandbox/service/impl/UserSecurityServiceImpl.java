package com.exadel.sandbox.service.impl;

import com.exadel.sandbox.dto.request.UserDto;
import com.exadel.sandbox.exceptions.EntityNotFoundException;
import com.exadel.sandbox.exceptions.messages.ExceptionMessage;
import com.exadel.sandbox.model.user.User;
import com.exadel.sandbox.repository.UserRepository;
import com.exadel.sandbox.service.DetailsUser;
import com.exadel.sandbox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserSecurityServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        DetailsUser detailsUser = DetailsUser.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .isEnabled(true)
                .role(Set.of(user.getRole()))
                .build();
        return detailsUser;
    }

    public UserDetails loadUserByUsernameName(String name) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(name);
        DetailsUser detailsUser = DetailsUser.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .isEnabled(true)
                .role(Set.of(user.getRole()))
                .build();
        return detailsUser;
    }

    @Override
    public UserDto findByName(String name) {
        User user = userRepository.findByEmail(name);
        return UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .location(user.getLocation())
                .role(user.getRole())
                .build();
    }

    @Override
    public Long findUserIdByUsername(String username) {
        return userRepository.findUserIdByUsername(username)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("%s%s", ExceptionMessage.USER_ID_BY_USERNAME_NOT_FOUND, username)
                        )
                );
    }

    @Override
    public DetailsUser findUser(String email) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

}