package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserRepository {
    List<UserDto> getUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(long id, UserDto userDto);

    UserDto getUserById(long id);

    void deleteUserById(long id);
}
