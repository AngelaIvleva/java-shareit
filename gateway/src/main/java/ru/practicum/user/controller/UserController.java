package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.ToCreate;
import ru.practicum.user.ToUpdate;
import ru.practicum.user.client.UserClient;
import ru.practicum.user.dto.UserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(ToCreate.class) @RequestBody UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @Validated(ToUpdate.class) @RequestBody UserDto userDto) {
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userClient.deleteUserById(userId);
    }
}
