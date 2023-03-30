package com.example.lab4.domain.validators;


import com.example.lab4.domain.User;
import com.example.lab4.domain.exceptions.ValidationException;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User user) throws ValidationException {
        String errors = "";
        if(user.getFirstName().isEmpty()){
            errors += "Prenumele trebuie completat!\n";
        }
        if(user.getLastName().isEmpty()){
            errors += "Numele trebuie completat!\n";
        }

        boolean firstName = user.getFirstName().chars().allMatch(Character::isLetter);
        if(!firstName){
            errors += "Prenumele trebuie sa contina doar litere!\n";
        }
        boolean lastName = user.getLastName().chars().allMatch(Character::isLetter);
        if(!lastName){
            errors += "Numele trebuie sa contina doar litere!\n";
        }

        if(errors.length()>0)
            throw new ValidationException("\n" + errors);

    }
}
