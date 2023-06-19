package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    BookingServiceImpl bookingService;

    public final String header = "X-Sharer-User-Id";

    User user1 = User.builder()
            .id(1L)
            .email("email@ex.com")
            .name("Harry Potter")
            .build();

    User user2 = User.builder()
            .id(2L)
            .email("email@exx.com")
            .name("Hermione Granger")
            .build();

    Item item = Item.builder()
            .id(1L)
            .name("Wand")
            .description("A wand is the object through which a witch or wizard channels his or her magic")
            .available(Boolean.TRUE)
            .owner(user1)
            .build();

    Booking booking = Booking.builder()
            .id(1L)
            .item(item)
            .booker(user2)
            .build();

    Item item2 = Item.builder()
            .id(2L)
            .name("Golden snitch")
            .description("It's the most important ball of the lot")
            .available(Boolean.TRUE)
            .owner(user1)
            .build();

    BookingOutputDto booking1 = BookingOutputDto.builder()
            .id(2L)
            .item(item2)
            .booker(user2)
            .build();

    BookingDto bookingDto = BookingMapper.toBookingDto(booking);

    BookingOutputDto bookingOutputDto = BookingMapper.toBookingOutputDto(booking);

    @Test
    void shouldAddBooking() throws Exception {

        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        when(bookingService.addBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingOutputDto);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header(header, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Wand"))
                .andExpect(jsonPath("$.booker.name").value("Hermione Granger"));

        verify(bookingService, Mockito.times(1))
                .addBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    void shouldChangeStatus() throws Exception {
        bookingOutputDto.setStatus(Status.APPROVED);
        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingOutputDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, Mockito.times(1))
                .changeStatus(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void shouldGetBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingOutputDto);

        mvc.perform(get("/bookings/1")
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Wand"));

        verify(bookingService, Mockito.times(1))
                .getBookingById(anyLong(), anyLong());
    }

    @Test
    void shouldGetAllByBookerWhenListIsEmpty() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings")
                        .header(header, 1)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, Mockito.times(1))
                .getAllByBooker(anyLong(), any(State.class), anyInt(), anyInt());
    }

    @Test
    void shouldGetAllByBooker() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutputDto, booking1));

        mvc.perform(get("/bookings")
                        .header(header, 1)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("Wand"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].item.name").value("Golden snitch"));

        verify(bookingService, Mockito.times(1))
                .getAllByBooker(anyLong(), any(State.class), anyInt(), anyInt());
    }

    @Test
    void shouldGetAllByOwnerWhenListIsEmpty() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings/owner")
                        .header(header, 1)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, Mockito.times(1))
                .getAllByOwner(anyLong(), any(State.class), anyInt(), anyInt());
    }

    @Test
    void shouldGetAllByOwner() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutputDto, booking1));

        mvc.perform(get("/bookings/owner")
                        .header(header, 1)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL"))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("Wand"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].item.name").value("Golden snitch"));

        verify(bookingService, Mockito.times(1))
                .getAllByOwner(anyLong(), any(State.class), anyInt(), anyInt());
    }
}