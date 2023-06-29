package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    UserDto getUserById(Long id);

    void deleteUserById(Long id);
}
