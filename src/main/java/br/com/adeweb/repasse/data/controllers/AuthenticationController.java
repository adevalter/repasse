package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.AuthenticationDTO;
import br.com.adeweb.repasse.data.models.AuthenticationResponse;
import br.com.adeweb.repasse.data.models.UsersDTO;
import br.com.adeweb.repasse.domain.entities.User;
import br.com.adeweb.repasse.domain.services.TokenService;
import br.com.adeweb.repasse.domain.services.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final  TokenService tokenService;
    private final  UsersService usersService;

    public AuthenticationController(AuthenticationManager authenticationManager, TokenService tokenService, UsersService usersService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usersService = usersService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationDTO authData){
       try {

           UsernamePasswordAuthenticationToken usernamePassword  = new UsernamePasswordAuthenticationToken(authData.username(), authData.password());
           Authentication login = authenticationManager.authenticate(usernamePassword);
           var token = tokenService.gerarToken((User) login.getPrincipal());
           return ResponseEntity.ok(new AuthenticationResponse(token));

       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public UsersDTO addUsers(@RequestBody UsersDTO usersDTO){

        return usersService.salvar(usersDTO);
    }
}

