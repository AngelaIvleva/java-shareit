package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return userRepository.createUser(userDto);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        return userRepository.updateUser(id, userDto);
    }

    @Override
    public UserDto getUserById(long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public void deleteUserById(long id) {
        userRepository.deleteUserById(id);
    }

}
