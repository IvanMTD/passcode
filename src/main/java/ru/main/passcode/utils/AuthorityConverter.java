package ru.main.passcode.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.main.passcode.models.Authority;
import ru.main.passcode.services.AuthorityService;

@Component
public class AuthorityConverter implements Converter<String, Authority> {

    private final AuthorityService authorityService;

    public AuthorityConverter(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @Override
    public Authority convert(String value) {
        long id = Long.parseLong(value);
        return authorityService.findById(id);
    }
}
