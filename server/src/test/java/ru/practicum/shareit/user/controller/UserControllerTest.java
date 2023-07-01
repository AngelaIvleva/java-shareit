package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.controller.UserController;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    UserServiceImpl userService;

    UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Harry Potter")
            .email("email@ex.com")
            .build();

    UserDto userUpd = UserDto.builder()
            .id(1L)
            .name("H.J.P.")
            .email("email@ex.com")
            .build();

    @Test
    void shouldGetUsers() throws Exception {
        when(userService.getUsers()).thenReturn(Collections.singletonList(userDto));

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Harry Potter"))
                .andExpect(jsonPath("$[0].email").value("email@ex.com"));

        verify(userService, Mockito.times(1)).getUsers();
    }

    @Test
    void shouldCreateUser() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        verify(userService, Mockito.times(1))
                .createUser(any(UserDto.class));
    }

    @Test
    void shouldUpdateUserWhenUserFound() throws Exception {
        when(userService.updateUser(anyLong(), any(UserDto.class)))
                .thenReturn(userUpd);

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userUpd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userUpd)))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("H.J.P."))
                .andExpect(jsonPath("$.email").value("email@ex.com"));

        verify(userService, Mockito.times(1))
                .updateUser(anyLong(), any(UserDto.class));
    }

    @Test
    void shouldNotUpdateUserAndThrowExceptionWhenUserNotFound() throws Exception {
        when(userService.updateUser(anyLong(), any(UserDto.class)))
                .thenThrow(new NotFoundException("User not found"));

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userUpd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("User not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        verify(userService, Mockito.times(1))
                .updateUser(anyLong(), any(UserDto.class));

    }

    @Test
    void shouldGetUserByIdWhenUserFound() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Harry Potter"))
                .andExpect(jsonPath("$.email").value("email@ex.com"));

        verify(userService, Mockito.times(1))
                .getUserById(anyLong());
    }

    @Test
    void shouldNotGetUserByIdAndThrowExceptionWhenUserNotFound() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        mvc.perform(get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("User not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }


    @Test
    void shouldDeleteUserById() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, Mockito.times(1))
                .deleteUserById(anyLong());
    }
}