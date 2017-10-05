package com.blazebit.validation.constraint;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static com.blazebit.validation.ConstraintValidatorUtils.containsViolation;
import static com.blazebit.validation.ConstraintValidatorUtils.getValidator;
import static org.junit.Assert.assertTrue;

public class CheckCompareTest {

    private Validator validator;

    @Before
    public void before() {
        validator = getValidator();
    }

    @Test
    public void testSimpleDefaultAndCompositeValidation() {
        Set<ConstraintViolation<User>> violations;
        User o;

        o = new User();
        violations = validator.validate(o);
        assertTrue(violations.size() == 2);
        assertTrue(containsViolation(violations, "primaryEmail"));
        assertTrue(containsViolation(violations, "secondaryEmail"));
    }

    @Test
    public void testSimpleDefaultAndCompositeValidation1() {
        Set<ConstraintViolation<UserComplex>> violations;
        UserComplex o;

        o = new UserComplex();
        violations = validator.validate(o);
        assertTrue(violations.size() == 2);
        assertTrue(containsViolation(violations, "primaryEmail"));
        assertTrue(containsViolation(violations, "secondaryEmail"));

        o = new UserComplex();
        o.primaryEmail = new Email();
        o.secondaryEmail = new Email();
        violations = validator.validate(o);
        assertTrue(violations.size() == 2);
        assertTrue(containsViolation(violations, "primaryEmail.email"));
        assertTrue(containsViolation(violations, "secondaryEmail.email"));

        o = new UserComplex();
        o.primaryEmail = new Email();
        o.primaryEmail.email = "";
        o.secondaryEmail = new Email();
        o.secondaryEmail.email = "";
        violations = validator.validate(o);
        assertTrue(violations.isEmpty());
    }

    public static class Email {
        @NotNull
        String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class BaseUser {
        @NotNull
        @Valid
        Email primaryEmail;
        @NotNull
        @Valid
        Email secondaryEmail;

        public Email getPrimaryEmail() {
            return primaryEmail;
        }

        public void setPrimaryEmail(Email primaryEmail) {
            this.primaryEmail = primaryEmail;
        }

        public Email getSecondaryEmail() {
            return secondaryEmail;
        }

        public void setSecondaryEmail(Email secondaryEmail) {
            this.secondaryEmail = secondaryEmail;
        }
    }

    @CheckCompare({"primaryEmail", "secondaryEmail"})
    public static class User extends BaseUser {
    }

    @CheckCompare({"primaryEmail.email", "secondaryEmail.email"})
    public static class UserComplex extends BaseUser {
    }

}
