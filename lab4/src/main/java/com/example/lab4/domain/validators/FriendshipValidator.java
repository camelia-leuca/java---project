package com.example.lab4.domain.validators;


import com.example.lab4.domain.Friendship;
import com.example.lab4.domain.exceptions.ValidationException;

public class FriendshipValidator implements Validator<Friendship>{

    @Override
    public void validate(Friendship friendship) throws ValidationException {
        String errors = "";
        if(friendship.getFirstId() == null){
            errors += "Id-ul primului utilizator trebuie completat!\n";
        }
        if(friendship.getSecondId() == null){
            errors += "Id-ul celui de-al doilea utilizator trebuie completat!\n";
        }

        if(friendship.getFirstId() == friendship.getSecondId())
                errors += "Id-urile utilizatorilor trebuie sa fie diferite!\n";

        if(errors.length()>0)
            throw new ValidationException("\n" + errors);
    }
}
