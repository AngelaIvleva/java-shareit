package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    UserRepository userRepository;

    User user1 = User.builder()
            .email("email@ex.com")
            .name("Harry Potter")
            .build();

    User user2 = User.builder()
            .email("email@exx.com")
            .name("Hermione Granger")
            .build();

    @Test
    void shouldFindByEmailContainingIgnoreCase() {
        em.persist(user1);
        em.persist(user2);

        List<User> users = userRepository.findByEmailContainingIgnoreCase("Email@ex.Com");
        assertEquals(1, users.size());
        assertEquals(user1.getId(), users.get(0).getId());

    }
}