package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.request.controller.ItemRequestController;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.request.mapper.ItemRequestMapper;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.service.ItemRequestService;
import ru.practicum.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemRequestService itemRequestService;

    private final String header = "X-Sharer-User-Id";

    User user = User.builder()
            .id(1L)
            .email("email@ex.com")
            .name("Harry Potter")
            .build();

    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("Need a wand with core of phoenix")
            .created(LocalDateTime.now())
            .requestor(user)
            .build();

    ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);

    ItemRequestResponseDto response = ItemRequestMapper.mapToItemRequestResponseDto(itemRequest);

    @Test
    void shouldCreateItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(header, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));

        verify(itemRequestService, Mockito.times(1))
                .createItemRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void shouldGetItemRequestsWhenRequestExists() throws Exception {
        when(itemRequestService.getItemRequests(anyLong()))
                .thenReturn(Collections.singletonList(response));

        mvc.perform(get("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(header, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(response))));

        verify(itemRequestService, Mockito.times(1))
                .getItemRequests(anyLong());
    }

    @Test
    void shouldGetEmptyItemRequestsListWhenRequestNotExists() throws Exception {
        when(itemRequestService.getItemRequests(anyLong()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/requests").content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(header, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService, Mockito.times(1))
                .getItemRequests(anyLong());
    }

    @Test
    void shouldGetEmptyAllItemRequestsListWhenRequestNotExists() throws Exception {
        when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService, Mockito.times(1))
                .getAllItemRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldGetAllItemRequestsListWhenRequestExists() throws Exception {
        when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(response));

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(header, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(response))));

        verify(itemRequestService, Mockito.times(1))
                .getAllItemRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldGetItemRequestById() throws Exception {
        response.setItems(Collections.emptyList());
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(response);

        mvc.perform(get("/requests/1")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(header, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(response.getDescription()), String.class))
                .andExpect(jsonPath("$.items", is(response.getItems()), List.class));

        verify(itemRequestService, Mockito.times(1))
                .getItemRequestById(anyLong(), anyLong());
    }
}
