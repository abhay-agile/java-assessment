package com.abhay.assessment.java_assessment.common.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Year;

public class CommonUtil {

    public static String getLoggedInUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else {
            return principal.toString();
        }
    }

    public static String generateEnrollmentId () {
        String prefix = "GTU";
        int year = Year.now().getValue();

        String sequence = String.format("%0" + 4 + "d", 1);

        return "GTU" + year + sequence;
    }

    public static String generateEnrollmentId (String enrollmentNumber) {
        String prefix = "GTU";
        int year = Year.now().getValue();

        String numberPart = enrollmentNumber.replaceAll("\\D", "");

        int lastNumber = Integer.parseInt(numberPart.substring(4));

        int nextNumber = lastNumber + 1;
        String sequence = String.format("%0" + 4 + "d", nextNumber);

        return "GTU" + year + sequence;
    }
}
