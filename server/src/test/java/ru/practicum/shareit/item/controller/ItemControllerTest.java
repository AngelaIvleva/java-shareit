package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.controller.ItemController;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemBookingDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Item;
import ru.practicum.item.service.ItemServiceImpl;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemServiceImpl itemService;

    public final String header = "X-Sharer-User-Id";

    UserDto userDto = UserDto.builder()
            .id(1L)
            .email("email@ex.com")
            .name("Harry Potter")
            .build();

    ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Wand")
            .description("A wand is the object through which a witch or wizard channels his or her magic")
            .available(Boolean.TRUE)
            .requestId(userDto.getId())
            .build();

    ItemDto itemUpd = ItemDto.builder()
            .id(1L)
            .name("broken wand")
            .description("A wand is broken")
            .available(Boolean.TRUE)
            .requestId(userDto.getId())
            .build();

    User user = UserMapper.toUser(userDto);
    Item item = ItemMapper.toItem(itemDto, user);
    ItemBookingDto itemBookingDto = ItemMapper.toItemBookingDto(item);

    @Test
    void shouldCreateItem() throws Exception {
        when(itemService.createItem(1L, itemDto))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        verify(itemService, Mockito.times(1))
                .createItem(anyLong(), any(ItemDto.class));
    }

    @Test
    void shouldUpdateItemWhenItemFound() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(itemUpd);

        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemUpd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("broken wand"))
                .andExpect(jsonPath("$.description").value("A wand is broken"));

        verify(itemService, Mockito.times(1))
                .updateItem(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void shouldUpdateItemAndThrowExceptionWhenItemNotFound() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenThrow(new NotFoundException("Item not found"));

        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemUpd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("Item not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        verify(itemService, Mockito.times(1))
                .updateItem(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemBookingDto);

        mvc.perform(get("/items/1")
                        .content(objectMapper.writeValueAsString(itemBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemBookingDto)));

        verify(itemService, Mockito.times(1))
                .getItemById(anyLong(), anyLong());
    }

    @Test
    void shouldNotGetItemByIdAndThrowExceptionWhenItemNotFound() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Item not found"));

        mvc.perform(get("/items/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("Item not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void shouldGetItems() throws Exception {
        when(itemService.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemBookingDto));

        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Wand"))
                .andExpect(jsonPath("$[0].description").value(
                        "A wand is the object through which a witch or wizard channels his or her magic"));

        verify(itemService, Mockito.times(1))
                .getItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldSearchItem() throws Exception {
        when(itemService.searchItem(anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemDto));

        mvc.perform(get("/items/search?text=wand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Wand"))
                .andExpect(jsonPath("$[0].requestId").value(1L));

        verify(itemService, Mockito.times(1))
                .searchItem(anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldCreateComment() throws Exception {
        UserDto user = UserDto.builder()
                .id(2L)
                .email("email@email.com")
                .name("Ron")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("that's ok")
                .authorName(user.getName())
                .build();

        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("that's ok"))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));

        verify(itemService, Mockito.times(1))
                .createComment(anyLong(), anyLong(), any());
    }
}