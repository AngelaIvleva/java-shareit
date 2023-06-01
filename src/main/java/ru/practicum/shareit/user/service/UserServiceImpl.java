package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException(String.format("Пользователь %d не найден", id));
        });
        List<User> list = userRepository.findByEmailContainingIgnoreCase(userDto.getEmail());
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (!list.isEmpty() && !user.getEmail().equals(userDto.getEmail())) {
            throw new ValidationException(String.format("Пользователь с email %s уже существует", userDto.getEmail()));
        } else {
            if (userDto.getEmail() != null) {
                user.setEmail(userDto.getEmail());
            }
            return UserMapper.toUserDto(userRepository.save(user));
        }
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("User with id {} not found", id);
            throw new NotFoundException(String.format("Пользователь id %d не найден", id));
        });
        return UserMapper.toUserDto(user);

    }

    @Override
    public void deleteUserById(long id) {
        userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException(String.format("Пользователь id %d не найден", id));
        });
        this.userRepository.deleteById(id);
    }

}
