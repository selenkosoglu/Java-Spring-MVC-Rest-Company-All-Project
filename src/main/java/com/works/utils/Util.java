package com.works.utils;

import com.works.entities.Company;
import com.works.repositories._jpa.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;

public class Util {

    public static Company theCompany;

    public static boolean isEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public static boolean isTel(String tel) {
        String regex = "^\\d{11}$";
        return tel.matches(regex);
    }

    public static final Integer pageSize = 10;

    public static List<Map<String, String>> errors(BindingResult bResult) {
        List<Map<String, String>> ls = new LinkedList<>();

        bResult.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String fieldMessage = error.getDefaultMessage();

            Map<String, String> erhm = new HashMap<>();
            erhm.put("fieldName", fieldName);
            erhm.put("fieldMessage", fieldMessage);
            ls.add(erhm);
        });
        return ls;
    }

    public static final String string = "java.lang.String";
    public static final String integer = "java.lang.Integer";
    public static final String date = "java.util.Date";

    public static final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    public static final String BASE_URL = "http://localhost:8091/";

}
