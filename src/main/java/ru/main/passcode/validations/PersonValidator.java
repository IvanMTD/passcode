package ru.main.passcode.validations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.main.passcode.dto.PersonDTO;
import ru.main.passcode.models.Person;
import ru.main.passcode.services.PersonService;

@Component
@RequiredArgsConstructor
public class PersonValidator implements Validator {
    private final PersonService personService;
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PersonDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PersonDTO personDTO = (PersonDTO) target;
        if(!personDTO.getPassword().equals(personDTO.getPasswordConfirm())){
            errors.rejectValue("passwordConfirm","","Подтверждение пароля не совпадает с введенным паролем");
        }
        for(Person person : personService.findAll()){
            if(person.getUsername().equals(personDTO.getUsername())){
                errors.rejectValue("username","","Этот e-mail уже занят");
            }
        }
    }
}
