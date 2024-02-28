package com.practice.jwt_Authentication.controller;

import com.practice.jwt_Authentication.Entity.User;
import com.practice.jwt_Authentication.payloads.requests.LoginRequest;
import com.practice.jwt_Authentication.payloads.response.JwtResponse;
import com.practice.jwt_Authentication.payloads.response.MessageResponse;
import com.practice.jwt_Authentication.repository.UserRepository;
import com.practice.jwt_Authentication.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/auth/register")
    public ResponseEntity<?> registerUser(@RequestBody User user){

        if (userRepository.existsByUsername(user.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Username is already use"));
        }

        if (userRepository.existsByEmail(user.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use"));
        }

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(newUser);

        return ResponseEntity.ok(new MessageResponse("User Created Successfully."));
    }


    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = userRepository.findByUsername(request.getUsername()).orElse(null);

        return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getUsername(), user.getEmail()));                         //send jwt token to user
    }
}
