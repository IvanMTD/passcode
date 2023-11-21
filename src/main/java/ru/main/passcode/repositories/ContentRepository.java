package ru.main.passcode.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.main.passcode.models.Content;

import java.util.List;

public interface ContentRepository extends CrudRepository<Content,Long> {
    List<Content> findAll();
    List<Content> findAllByOrderByPlacedAtDesc(Pageable pageable);
    List<Content> findAllByOrderByIdDesc(Pageable pageable);
    List<Content> findAllByOrderByIdDesc();
    Content findFirstByOrderByIdDesc();
}
