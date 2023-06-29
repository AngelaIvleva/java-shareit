package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.item.dto.ItemBookingDto;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.item.service.ItemServiceImpl;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext
class ItemServiceImplIntegrationTest {

    @Autowired
    ItemServiceImpl itemService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    User user1 = User.builder()
            .id(1L)
            .email("email@ex.com")
            .name("Harry Potter")
            .build();

    Item item1 = Item.builder()
            .id(1L)
            .name("Wand")
            .description("A wand is the object through which a witch or wizard channels his or her magic")
            .available(Boolean.TRUE)
            .owner(user1)
            .build();

    Item item2 = Item.builder()
            .id(2L)
            .name("Broken wand")
            .description("the wand is broken")
            .available(Boolean.TRUE)
            .owner(user1)
            .build();

    @Test
    void shouldGetItems() {
        userRepository.save(user1);
        itemRepository.save(item1);
        itemRepository.save(item2);

        List<ItemBookingDto> userItems = itemService.getItems(1L, 0, 10);

        assertAll(() -> assertEquals(item1.getDescription(), userItems.get(0).getDescription()),
                () -> assertEquals(item2.getName(), userItems.get(1).getName()));
    }

}