package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.request.service.ItemRequestServiceImpl;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    ItemRequestRepository requestRepository;

    @Autowired
    ItemRequestServiceImpl requestService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

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
    void shouldGetItemRequests() {
        userRepository.save(user);
        requestRepository.save(itemRequest);
        itemRepository.save(item);

        List<ItemRequestResponseDto> responseDtoList = requestService.getItemRequests(user.getId());

        assertAll(() -> assertEquals(itemRequest.getDescription(), responseDtoList.get(0).getDescription()),
                () -> assertEquals(itemRequest.getId(), responseDtoList.get(0).getId()),
                () -> assertEquals(1, responseDtoList.size()));

    }
}