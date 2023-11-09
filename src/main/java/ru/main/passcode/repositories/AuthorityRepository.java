package ru.main.passcode.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.main.passcode.models.Authority;

import java.util.List;

public interface AuthorityRepository extends CrudRepository<Authority,Long> {
    List<Authority> findAll();
}
