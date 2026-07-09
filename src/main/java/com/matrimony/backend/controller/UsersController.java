package com.matrimony.backend.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.matrimony.backend.DTO.MatchDTO;
import com.matrimony.backend.Model.UsersDetail;
import com.matrimony.backend.Service.UsersService;


@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UsersController {

    @Autowired
    private UsersService usersService;

    // GET ALL USERS
    @GetMapping
    public List<MatchDTO> getAllUsers() {
        return usersService.getAllUsers();
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public UsersDetail getUserById(@PathVariable Long id) {
        return usersService.getUserById(id);
    }

    // CREATE USER
    @PostMapping("/{currentUserId}")
    public UsersDetail createUser(
            @RequestBody UsersDetail user,@PathVariable Long currentUserId) {

        return usersService.createUser(user,currentUserId);
    }

    // UPDATE USER
    @PutMapping("/{id}")
    public UsersDetail updateUser(
            @PathVariable Long id,
            @RequestBody UsersDetail user) {

        return usersService.updateUser(id, user);
    }
}