package com.example.form;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.utils.SampleSignupForm.createSignupForm;
import static org.assertj.core.api.Assertions.assertThat;

class SignupFormTest {
    @Autowired
    Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nested
    class ValidGroupA {
        @Test
        @DisplayName("準正常系: NotBlankに空白が含まれていた場合、エラーが存在すること")
        void notBlank() {
            SignupForm signupForm = createSignupForm();
            signupForm.setUserId("");
            signupForm.setPassword("");
            signupForm.setUserName("");

            Set<ConstraintViolation<SignupForm>> result = validator.validate(signupForm, ValidGroup1.class);

            List<String> actual = result.stream()
                    .map(v -> v.getPropertyPath().toString())
                    .collect(Collectors.toList());

            assertThat(actual).containsOnly("userId", "password", "userName");
        }
    }
}
