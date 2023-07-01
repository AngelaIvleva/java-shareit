package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException(String.format("Пользователь %d не найден", id));
        });

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            List<User> list = userRepository.findByEmailContainingIgnoreCase(userDto.getEmail());
            if (!list.isEmpty() && !user.getEmail().equals(userDto.getEmail())) {
                throw new ValidationException(String.format("Пользователь с email %s уже существует", userDto.getEmail()));
            } else {
                user.setEmail(userDto.getEmail());
            }
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("User with id {} not found", id);
            throw new NotFoundException(String.format("Пользователь id %d не найден", id));
        });
        return UserMapper.toUserDto(user);

    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException(String.format("Пользователь id %d не найден", id));
        });
        this.userRepository.deleteById(id);
    }

}
