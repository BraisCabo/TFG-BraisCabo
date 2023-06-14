package com.tfg.brais.ComplementaryServiceTests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.tfg.brais.Service.ComplementaryServices.EmailValidator;

public class EmailValidatorTest {

    private EmailValidator emailValidator = new EmailValidator();

    @Test
    public void testValidEmails() throws Exception {
        List<String> emails = new ArrayList<String>();
        // valid email addresses
        emails.add("alice@example.com");
        emails.add("alice.bob@example.co.in");
        emails.add("alice1@example.me.org");
        emails.add("alice_bob@example.com");
        emails.add("alice-bob@example.com");
        emails.add("alice@example.company.in");
        for (String email : emails) {
            assertTrue(emailValidator.validate(email));
        }
    }

    @Test
    public void testInvalidEmails() throws Exception {
        List<String> emails = new ArrayList<String>();
        // invalid email addresses
        emails.add("alice.example.com");
        emails.add("alice@example@com");
        emails.add("@example.com");
        emails.add("alice@example.com.");
        emails.add("alice@example..com");
        emails.add(".alice@example.com");
        emails.add("alice@example.com.");
        emails.add("alice@example.c");
        emails.add("alice@example.company");
        for (String email : emails) {
            assertFalse(emailValidator.validate(email));
        }
    }
}
