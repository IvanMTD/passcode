package ru.main.passcode.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.main.passcode.dto.PersonDTO;
import ru.main.passcode.models.Person;
import ru.main.passcode.repositories.PersonRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService implements UserDetailsService {
    private final PersonRepository personRepository;
    private final AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> optionalPerson = personRepository.findByUsername(username);
        if(optionalPerson.isEmpty()){
            throw new UsernameNotFoundException("Subject '" + username + "' is not found");
        }
        return optionalPerson.get();
    }

    public Person save(PersonDTO personDTO) {
        Person person = new Person();
        person.setUsername(personDTO.getUsername());
        person.setPassword(passwordEncoder.encode(personDTO.getPassword()));
        person.setFirstName(personDTO.getFirstName());
        person.setLastname(personDTO.getLastname());
        person.setAuthority(authorityService.findById(personDTO.getAuthority().getId()));
        return personRepository.save(person);
    }

    public List<PersonDTO> findAllByPageable(Pageable pageable) {
        List<Person> persons = personRepository.findAllByOrderByPlacedAtDesc(pageable);
        List<PersonDTO> newPersonList = new ArrayList<>();
        for(Person person : persons){
            PersonDTO personDTO = new PersonDTO();
            personDTO.setId(person.getId());
            personDTO.setUsername(person.getUsername());
            personDTO.setFirstName(person.getFirstName());
            personDTO.setLastname(person.getLastname());
            personDTO.setUsername(person.getUsername());
            personDTO.setAuthority(person.getAuthority());
            newPersonList.add(personDTO);
        }
        return newPersonList;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public void delete(long id) {
        personRepository.deleteById(id);
    }
}
