package es.udc.ws.app.model.util;

import es.udc.ws.util.exceptions.InputValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class CustomPropertyValidator {

    public static void validateEmail(String email) throws InputValidationException{
        if(email ==  null) throw new InputValidationException("Null email");
        String regexPattern = "^(.+)@(\\S+)$";
        if(!Pattern.compile(regexPattern).matcher(email).matches())
            throw new InputValidationException("Invalid Email: "+email);
    }

    public static void validateDate(LocalDateTime date) throws InputValidationException {
        if (date.isBefore(LocalDateTime.now())){
            throw new InputValidationException("Invalid date: "+date.toString());
        }
    }

    public static void validateVisitorName(String name) throws InputValidationException {
        if (name == null || name.isEmpty()){
            throw new InputValidationException("Invalid visitor name: "+name);
        }
    }
}
