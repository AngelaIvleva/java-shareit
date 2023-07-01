package ru.practicum.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorId(Long userId);

    @Query("select ir from ItemRequest ir " +
            "where ir.requestor.id != ?1")
    List<ItemRequest> findAllPageable(Long userId, Pageable p);
}
