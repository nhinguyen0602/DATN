package com.backend.helpdesk.controller;

import com.backend.helpdesk.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @Secured("ROLE_EMPLOYEES")
    @GetMapping
    public ResponseEntity<?> login(@RequestHeader("token-google") String tokenGoogle) throws IOException, GeneralSecurityException {
        String email = authenticationService.getEmailFromTokenUser(tokenGoogle);
        return authenticationService.generateToken(email);
    }
}
