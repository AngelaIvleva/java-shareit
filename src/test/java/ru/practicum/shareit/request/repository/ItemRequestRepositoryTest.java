package ru.practicum.shareit.request.repository;

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
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user1 = User.builder()
            .email("email@ex.com")
            .name("Harry Potter")
            .build();

    User user2 = User.builder()
            .email("email@exx.com")
            .name("Harry")
            .build();

    ItemRequest itemRequest = ItemRequest.builder()
            .description("Need a wand with core of phoenix")
            .created(LocalDateTime.now())
            .requestor(user2)
            .build();

    Item item = Item.builder()
            .name("Wand")
            .description("A wand is the object through which a witch or wizard channels his or her magic")
            .available(Boolean.TRUE)
            .owner(user1)
            .itemRequest(itemRequest)
            .build();
    @Test
    void shouldFindAllByRequestorId() {
        em.persist(user1);
        em.persist(user2);
        em.persist(itemRequest);
        em.persist(item);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(user2.getId());
        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest.getId(), itemRequests.get(0).getId());
    }

    @Test
    void shouldFindAllPageable() {
        em.persist(user1);
        em.persist(user2);
        em.persist(itemRequest);
        em.persist(item);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllPageable(user1.getId(), Pageable.ofSize(1));
        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest.getId(), itemRequests.get(0).getId());
    }
}