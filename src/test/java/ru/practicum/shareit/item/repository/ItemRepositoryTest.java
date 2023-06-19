package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    ItemRepository itemRepository;

    User user1 = User.builder()
            .email("email@exx.com")
            .name("Hermione Granger")
            .build();

    User user2 = User.builder()
            .email("email@ex.com")
            .name("Harry")
            .build();

    ItemRequest itemRequest = ItemRequest.builder()
            .description("Need a wand with core of phoenix")
            .created(LocalDateTime.now())
            .requestor(user2)
            .build();

    Item item1 = Item.builder()
            .name("Wand")
            .description("A wand is the object through which a witch or wizard channels his or her magic")
            .available(Boolean.TRUE)
            .owner(user1)
            .itemRequest(itemRequest)
            .build();

    Item item2 = Item.builder()
            .name("broken wand")
            .description("A wand is broken")
            .available(Boolean.TRUE)
            .owner(user1)
            .build();


    @Test
    void shouldSearchItemsByText() {
        em.persist(user1);
        em.persist(user2);
        em.persist(itemRequest);
        em.persist(item1);
        em.persist(item2);

        List<Item> items = itemRepository
                .searchItemsByText("wand", Pageable.ofSize(2));
        assertEquals(2, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item2.getId(), items.get(1).getId());

    }

    @Test
    void shouldFindAllByRequestId() {
        em.persist(user1);
        em.persist(user2);
        em.persist(itemRequest);
        em.persist(item1);
        em.persist(item2);

        List<Item> items = itemRepository
                .findAllByRequestId(itemRequest.getId());
        assertEquals(1, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
    }

    @Test
    void shouldFindAllByOwnerIdOrderByIdAsc() {
        em.persist(user1);
        em.persist(user2);
        em.persist(itemRequest);
        em.persist(item1);
        em.persist(item2);

        List<Item> items = itemRepository
                .findAllByOwnerIdOrderByIdAsc(user1.getId(), Pageable.ofSize(1));
        assertEquals(1, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
    }
}