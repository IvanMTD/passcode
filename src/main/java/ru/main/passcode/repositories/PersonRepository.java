package ru.main.passcode.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.main.passcode.models.Person;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person,Long> {
    Optional<Person> findByUsername(String username);
    List<Person> findAll();
    List<Person> findAllByOrderByPlacedAtDesc(Pageable pageable);
    List<Person> findAllByOrderByIdDesc(Pageable pageable);
}
