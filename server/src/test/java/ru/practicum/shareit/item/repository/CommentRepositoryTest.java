package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.CommentRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    CommentRepository commentRepository;
    User user1 = User.builder()
            .email("email@exx.com")
            .name("Hermione Granger")
            .build();

    User user2 = User.builder()
            .email("email@ex.com")
            .name("Harry")
            .build();

    Item item1 = Item.builder()
            .name("Wand")
            .description("A wand is the object through which a witch or wizard channels his or her magic")
            .available(Boolean.TRUE)
            .owner(user1)
            .build();
    Comment comment = Comment.builder()
            .text("the wand is ok")
            .author(user2)
            .item(item1)
            .created(LocalDateTime.now())
            .build();

    @Test
    void shouldFindAllByItemId() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(comment);

        List<Comment> comments = commentRepository
                .findAllByItemId(item1.getId());
        assertEquals(1, comments.size());
        assertEquals(comment, comments.get(0));
    }
}