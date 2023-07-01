package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    User user1 = User.builder()
            .id(1L)
            .email("email@ex.com")
            .name("Harry Potter")
            .build();

    @Test
    void shouldGetUsers() {
        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(user1));

        List<UserDto> response = userService.getUsers();
        assertEquals(user1.getName(), response.get(0).getName());
    }

    @Test
    void createUser() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user1);

        UserDto userDto = userService.createUser(UserMapper.toUserDto(user1));

        assertEquals(user1.getId(), userDto.getId());
        assertEquals(user1.getName(), userDto.getName());
        verify(userRepository, Mockito.times(1))
                .save(any(User.class));
    }

    @Test
    void shouldUpdateUserWhenUserFound() {
        User user2 = User.builder()
                .id(1L)
                .email("email@exx.com")
                .name("Hermione Granger")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(userRepository.save(any(User.class)))
                .thenReturn(user2);

        UserDto userDto = userService.updateUser(user1.getId(), UserMapper.toUserDto(user2));

        assertEquals(user1.getId(), userDto.getId());
        assertEquals(user1.getName(), userDto.getName());
        verify(userRepository, Mockito.times(1))
                .save(any(User.class));
    }

    @Test
    void shouldNotUpdateUserAndThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.updateUser(user1.getId(), any(UserDto.class)));
        verify(userRepository, Mockito.never())
                .save(any(User.class));
    }

    @Test
    void shouldGetUserById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        UserDto userDto = userService.getUserById(user1.getId());

        assertEquals(user1.getId(), userDto.getId());
        assertEquals(user1.getName(), userDto.getName());
    }

    @Test
    void shouldNotGetUserByIdWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(user1.getId()));
    }

    @Test
    void shouldDeleteUserById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        userService.deleteUserById(user1.getId());

        verify(userRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    void shouldNotDeleteByIdWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.deleteUserById(user1.getId()));
    }
}