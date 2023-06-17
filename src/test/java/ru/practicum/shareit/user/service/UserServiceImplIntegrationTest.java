package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
class UserServiceImplIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServiceImpl userService;

    User user1 = User.builder()
            .id(1L)
            .email("email@ex.com")
            .name("Harry Potter")
            .build();
    @Test
    void shouldUpdateUser() {
        User user3 = User.builder()
                .name("Hermione")
                .build();
        userRepository.save(user1);

        UserDto userDto = userService.updateUser(user1.getId(), UserMapper.toUserDto(user3));

        assertAll(() -> assertEquals(userDto.getName(), "Hermione"),
                () -> assertEquals(userDto.getId(), user1.getId()),
                () -> assertEquals(userDto.getEmail(), user1.getEmail())
        );
    }
}