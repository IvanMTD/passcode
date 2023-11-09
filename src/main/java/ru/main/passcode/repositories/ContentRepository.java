package ru.main.passcode.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.main.passcode.models.Content;

import java.util.List;

public interface ContentRepository extends CrudRepository<Content,Long> {
    List<Content> findAll();
}
