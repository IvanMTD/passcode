package ru.main.passcode.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.main.passcode.models.Content;
import ru.main.passcode.models.Person;

import java.util.List;

public interface ContentRepository extends CrudRepository<Content,Long> {
    List<Content> findAll();
    List<Content> findAllByOrderByPlacedAtDesc(Pageable pageable);
    List<Content> findAllByOrderByIdDesc(Pageable pageable);
    Content findFirstByOrderByIdDesc();
}
