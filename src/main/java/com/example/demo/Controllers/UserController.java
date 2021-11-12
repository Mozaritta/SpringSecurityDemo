package com.example.demo.Controllers;

import lombok.extern.slf4j.Slf4j;
import com.example.demo.Models.User;
import com.example.demo.Models.Role;
import lombok.RequiredArgsConstructor;
import com.example.demo.Data.RoleToUserForm;
import com.example.demo.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

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

}
