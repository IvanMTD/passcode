package ru.main.passcode.validations;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.main.passcode.dto.PersonDTO;

@Component
public class PersonValidator implements Validator {
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
    }
}
