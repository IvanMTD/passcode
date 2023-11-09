package ru.main.passcode.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.main.passcode.models.Authority;
import ru.main.passcode.repositories.AuthorityRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;

    public List<Authority> findAll() {
        return authorityRepository.findAll();
    }

    public Authority findById(long id) {
        return authorityRepository.findById(id).orElse(null);
    }
}
