package com.example.demo.Repositories;

import com.example.demo.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
        Role findByName(String name);
        // spring wil create implementations of these two interfaces for us with the DAO methods findAll, findById ...

        }
