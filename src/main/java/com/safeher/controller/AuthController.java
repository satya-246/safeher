package com.safeher.controller;

import com.safeher.model.User;
import com.safeher.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Register User
    @PostMapping("/register")
    public User register(@RequestBody User user){

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        user.setPassword(encoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // Login User
    @PostMapping("/login")
    public User login(@RequestBody User loginUser){

        User user = userRepository.findByEmail(loginUser.getEmail());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(user != null && encoder.matches(loginUser.getPassword(), user.getPassword())){
            return user;
        }

        return null;
    }

}


