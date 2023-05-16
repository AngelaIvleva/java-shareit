package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, String> emails = new HashMap<>();
    private long id = 0;

    @Override
    public List<UserDto> getUsers() {
        return users.values().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkEmailForExistence(userDto);
        userDto.setId(++id);
        users.put(userDto.getId(), UserMapper.toUser(userDto));
        emails.put(userDto.getId(), userDto.getEmail());
        log.info("Пользователь {} id {} создан", userDto.getName(), userDto.getId());
        return userDto;
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        checkEmailForExistence(userDto);
        if (users.containsKey(id)) {
            if (userDto.getName() != null) {
                users.get(id).setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                users.get(id).setEmail(userDto.getEmail());
                emails.remove(id);
                emails.put(userDto.getId(), userDto.getEmail());
            }
            log.info("Пользователь {} обновлен", userDto.getName());
            return UserMapper.toUserDto(users.get(id));
        } else {
            throw new NotFoundException(String.format("Пользователь id %d  не найден", id));
        }
    }

    @Override
    public UserDto getUserById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователь id %d не найден", id));
        } else return UserMapper.toUserDto(users.get(id));
    }

    @Override
    public void deleteUserById(long id) {
        if (users.containsKey(id)) {
            emails.remove(id);
            users.remove(id);
            log.info("Пользователь id {} удален", id);
        } else {
            throw new NotFoundException(String.format("Пользователь id %d не найден", id));
        }
    }

    private void checkEmailForExistence(UserDto userDto) {
        if (emails.containsValue(userDto.getEmail())) {
            if (!emails.get(userDto.getId()).equals(userDto.getEmail())) {
                throw new ValidationException("Пользователь с таким email уже существует");
            }
        }
    }
}
