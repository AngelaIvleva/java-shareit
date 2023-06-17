package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    ItemRequestRepository requestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

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

    Item item = Item.builder()
            .id(1L)
            .name("Wand")
            .description("A wand is the object through which a witch or wizard channels his or her magic")
            .available(Boolean.TRUE)
            .owner(user)
            .itemRequest(itemRequest)
            .build();

    @Test
    void shouldCreateRequestWhenUserFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);
        when(requestRepository.save(any()))
                .thenReturn(itemRequest);

        ItemRequestDto requestDto = itemRequestService.createItemRequest(user.getId(), itemRequestDto);
        itemRequestDto.setCreated(requestDto.getCreated());

        assertEquals(itemRequestDto, requestDto);
        verify(requestRepository, Mockito.times(1))
                .save(any());
    }

    @Test
    void shouldNotCreateAndThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(1L, itemRequestDto));
        verify(requestRepository, never())
                .save(Mockito.any(ItemRequest.class));
    }

    @Test
    void shouldGetItemRequestsListWhenUserFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        List<ItemRequestResponseDto> responseList = itemRequestService.getItemRequests(user.getId());
        assertTrue(responseList.isEmpty());
        verify(requestRepository, Mockito.times(1))
                .findAllByRequestorId(anyLong());
    }

    @Test
    void shouldNotGetItemRequestsListAndThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequests(561L));
    }

    @Test
    void shouldGetAllItemRequestsWhenUserFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(requestRepository.findAllPageable(anyLong(), any()))
                .thenReturn(Collections.singletonList(itemRequest));

        List<ItemRequestResponseDto> responseList = itemRequestService.getAllItemRequests(1L, 0, 10);
        assertEquals(1, responseList.size());

        verify(requestRepository)
                .findAllPageable(anyLong(), any());

    }

    @Test
    void shouldNotGetAllItemRequestsAndThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllItemRequests(1L, 0, 10));
    }

    @Test
    void shouldGetItemRequestByIdWhenUserAndRequestFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));

        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(Collections.singletonList(item));

        ItemRequestResponseDto response = itemRequestService.getItemRequestById(user.getId(), itemRequest.getId());

        assertNotNull(response);
        verify(requestRepository)
                .findById(anyLong());
        verify(itemRepository)
                .findAllByRequestId(anyLong());
    }

    @Test
    void shouldNotGetItemRequestByIdAndTrowExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(1L, 1L));
    }

    @Test
    void shouldNotGetItemRequestByIdAndTrowExceptionWhenRequestNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(requestRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(1L, 1L));
    }
}
