package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemBookingDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.mapper.CommentMapper;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.CommentRepository;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.item.service.ItemServiceImpl;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;


    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    ItemServiceImpl itemService;

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

    @Test
    void shouldCreateItemWhenUserFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto itemDto = itemService.createItem(user1.getId(), ItemMapper.toItemDto(item));

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        verify(itemRepository, Mockito.times(1))
                .save(any(Item.class));
    }

    @Test
    void shouldNotCreateItemAndThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createItem(user1.getId(), ItemMapper.toItemDto(item)));
        verify(itemRepository, Mockito.never())
                .save(any(Item.class));

    }

    @Test
    void shouldUpdateItemWhenItemFound() {
        Item itemUpd = Item.builder()
                .id(1L)
                .name("broken wand")
                .description("the wand is broken")
                .available(Boolean.TRUE)
                .owner(user1)
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(item))
                .thenReturn(itemUpd);

        ItemDto itemDto = itemService.updateItem(user1.getId(), item.getId(), ItemMapper.toItemDto(itemUpd));

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), "broken wand");
        assertEquals(item.getName(), itemDto.getName());
        verify(itemRepository, Mockito.times(1))
                .save(any(Item.class));
    }

    @Test
    void shouldNotUpdateItemAndThrowExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(user1.getId(), item.getId(), ItemMapper.toItemDto(item)));
        verify(itemRepository, Mockito.never())
                .save(any(Item.class));
    }

    @Test
    void shouldNotUpdateItemAndThrowExceptionWhenUserIsNotOwner() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(user2.getId(), item.getId(), ItemMapper.toItemDto(item)));
        verify(itemRepository, Mockito.never())
                .save(any(Item.class));
    }

    @Test
    void shouldGetItemById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        ItemBookingDto itemDto = itemService.getItemById(user1.getId(), item.getId());

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
    }

    @Test
    void shouldNotGetItemByIdAndThrowExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(user2.getId(), item.getId()));
    }

    @Test
    void shouldNotGetItemByIdAndThrowExceptionWhenUserNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(user1.getId(), item.getId()));
    }

    @Test
    void shouldGetItems() {
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(any(Long.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(item));

        List<ItemBookingDto> response = itemService.getItems(user1.getId(), 0, 10);
        assertEquals(item.getName(), response.get(0).getName());
    }

    @Test
    void shouldSearchItemWhenTextIsNotBlank() {
        when(itemRepository.searchItemsByText(anyString(), any()))
                .thenReturn(Collections.singletonList(item));

        List<ItemDto> itemDto = itemService.searchItem("wand", 0, 10);
        assertEquals(1, itemDto.size());
        assertEquals(ItemMapper.toItemDto(item), itemDto.get(0));
    }

    @Test
    void shouldSearchItemEmptyListWhenTextIsBlank() {
        when(itemRepository.searchItemsByText(anyString(), any()))
                .thenReturn(Collections.emptyList());

        List<ItemDto> itemDto = itemService.searchItem("wand", 0, 10);
        assertTrue(itemDto.isEmpty());
    }

    @Test
    void shouldCreateComment() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .item(item)
                .booker(user2)
                .build();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("the wand is ok")
                .authorName(user2.getName())
                .created(LocalDateTime.now())
                .build();
        Comment comment = CommentMapper.toComment(commentDto, user2, item);
        comment.setText(commentDto.getText());
        when(bookingRepository.findFirstByItemAndBookerAndEndIsBeforeOrderByEnd(any(Item.class), any(User.class), any()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(commentRepository.save(any()))
                .thenReturn(comment);
        assertEquals(commentDto.getText(), itemService.createComment(item.getId(), user2.getId(), commentDto)
                .getText());
        verify(commentRepository, Mockito.times(1))
                .save(comment);
    }

    @Test
    void shouldNotCreateCommentAndThrowExceptionWhenUserNeverBooked() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(bookingRepository
                .findFirstByItemAndBookerAndEndIsBeforeOrderByEnd(any(Item.class), any(User.class), any()))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> itemService.createComment(item.getId(), user2.getId(), new CommentDto()));
    }
}