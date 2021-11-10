package com.example.demo.Services;

import com.example.demo.Models.Role;
import com.example.demo.Models.User;

import java.util.List;

public interface UserService {
    // I need a method that can save a user
    User saveUser(User user);
    Role saveRole(Role role);
    void affectRole(String username, String roleName);
    User getUser(String username);
    List<User>getUsers();
}
