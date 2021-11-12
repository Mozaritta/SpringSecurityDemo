package com.example.demo.Controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.Filters.CustomAuthenticationFilter;
import com.example.demo.Filters.CustomAuthorizationFilter;
import lombok.extern.slf4j.Slf4j;
import com.example.demo.Models.User;
import com.example.demo.Models.Role;
import lombok.RequiredArgsConstructor;
import com.example.demo.Data.RoleToUserForm;
import com.example.demo.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController{

    private final UserService userService;
    private final CustomAuthorizationFilter authorizationFilter;
    private final CustomAuthenticationFilter authenticationFilter;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUser(String user){
        return ResponseEntity.accepted().body(userService.getUser(user));
    }

    @PostMapping("/role/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
        return ResponseEntity.created(null).body(userService.saveRole(role));
    }

    @PostMapping("/user/save")
    public ResponseEntity<User>saveUser(@RequestBody User user){
        // using created instead of ok which is a 201 accepted which means something was created on
        // the server
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping("/role/to/user")
    public ResponseEntity<?> affectRole(@RequestBody RoleToUserForm form){
        userService.affectRole(form.getUserName(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
            try{
                String token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(token);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);
                String refreshToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withIssuer(request.getRequestURL().toString())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                authenticationFilter.mapping(refreshToken, response);

            }catch(Exception e){
                log.error(authorizationFilter.ERROR_LOGIN + e.getMessage());
                authorizationFilter.errorFunction(e, response);
            }
        } else {

        }
    }

}
