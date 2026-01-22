package com.insight_pulse.tech.utils;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.StringUtils;

public class EmailUtils {

    private static final EmailValidator validator = EmailValidator.getInstance();

    private EmailUtils() {
        throw new UnsupportedOperationException("Util class should not be instantiated");
    }
    
    public static boolean validateEmail(String email) {
        if(!StringUtils.hasText(email)) {
            return false;
        }
        return validator.isValid(email);
    }
}
