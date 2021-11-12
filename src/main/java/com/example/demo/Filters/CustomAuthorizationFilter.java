package com.example.demo.Filters;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.util.*;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    public final static String ERROR_LOGIN = "Error logging in : ";

    public ArrayList tokenFunction(String auth){
        String token = auth.substring("Bearer ".length());
        log.info(token);
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        ArrayList test = new ArrayList();
        test.add(roles);
        test.add(username);
        return test;
    }
    public void authorizationFunction(String authorizationHeader){
        ArrayList test = tokenFunction(authorizationHeader);
        String[] roles = (String[]) test.get(1);
        String username = (String) test.get(2);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        username, null, authorities
                );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    public void errorFunction(Exception e, HttpServletResponse response) throws IOException {
        response.setHeader("error", e.getMessage());
        response.setStatus(FORBIDDEN.value());
//        response.sendError(FORBIDDEN.value());
        Map<String, String> error = new HashMap<>();
        error.put("error_message", e.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if(request.getServletPath().equals("/login")){
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
                try{
                    authorizationFunction(authorizationHeader);
                }catch(Exception e){
                    log.error(ERROR_LOGIN + e.getMessage());

                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
