package com.example.form;

import static com.example.utils.SampleSignupForm.createSignupForm;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SignupFormTest {
    @Autowired
    Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }


    @Nested
    class ValidGroupPass {
        @Test
        @DisplayName("正常系：バリデーションチェックが成功した場合、エラーは存在しない。")
        void testValidation() {
            SignupForm signupForm = createSignupForm();

            Set<ConstraintViolation<SignupForm>> result =
                    validator.validate(signupForm);

            List<String> actual = result.stream()
                    .map(v -> v.getPropertyPath().toString())
                    .collect(Collectors.toList());

            assertThat(actual.size()).isEqualTo(0);
        }
    }

    @Nested
    class ValidGroupError {
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

        @Test
        @DisplayName("準正常系： NotNullにnullが含まれていた場合、エラーが存在すること")
        void testNotnull() {
            SignupForm signupForm = createSignupForm();
            signupForm.setBirthday(null);
            signupForm.setGender(null);

            Set<ConstraintViolation<SignupForm>> result = validator.validate(signupForm, ValidGroup1.class);

            List<String> actual = result.stream()
                    .map(v -> v.getPropertyPath().toString())
                    .collect(Collectors.toList());

            assertThat(actual).containsOnly("birthday", "gender");
        }

        @Test
        @DisplayName("準正常系：userIdがメール形式でなかった場合、エラーが存在すること")
        void testNotEmail() {
            SignupForm signupForm = createSignupForm();
            signupForm.setUserId("test");

            Set<ConstraintViolation<SignupForm>> result = validator.validate(signupForm, ValidGroup2.class);

            List<String> actual = result.stream()
                    .map(v -> v.getPropertyPath().toString())
                    .collect(Collectors.toList());

            assertThat(actual).containsOnly("userId");
        }

        @Test
        @DisplayName("準正常系：passwordの桁数が最大より大きかった場合、エラーが存在すること")
        void testPasswordMax() {
            SignupForm signupForm = createSignupForm();
            signupForm.setPassword("111111111122222222223333333333444444444455555555556666666666" +
                    "777777777788888888889999999999AAAAAAAAAAB");

            Set<ConstraintViolation<SignupForm>> result = validator.validate(signupForm, ValidGroup2.class);

            List<String> actual = result.stream()
                    .map(v -> v.getPropertyPath().toString())
                    .collect(Collectors.toList());

            assertThat(actual).containsOnly("password");
        }

        @Test
        @DisplayName("準正常系：passwordの桁数が最小より小さかった場合、エラーが存在すること")
        void testPasswordMin() {
            SignupForm signupForm = createSignupForm();
            signupForm.setPassword("aaa");

            Set<ConstraintViolation<SignupForm>> result = validator.validate(signupForm, ValidGroup2.class);

            List<String> actual = result.stream()
                    .map(v -> v.getPropertyPath().toString())
                    .collect(Collectors.toList());

            assertThat(actual).containsOnly("password");
        }

        @Test
        @DisplayName("準正常系：passwordに半角英数字以外が含まれいた場合、エラーが存在すること")
        void testPasswordHaileSize() {
            SignupForm signupForm = createSignupForm();
            signupForm.setPassword("テストテスト");

            Set<ConstraintViolation<SignupForm>> result = validator.validate(signupForm, ValidGroup2.class);

            List<String> actual = result.stream()
                    .map(v -> v.getPropertyPath().toString())
                    .collect(Collectors.toList());

            assertThat(actual).containsOnly("password");
        }

        @Test
        @DisplayName("準正常系：ageの値が最小値より小さい場合、エラーが存在すること")
        void testAgeMin() {
            SignupForm signupForm = createSignupForm();
            signupForm.setAge(19);

            Set<ConstraintViolation<SignupForm>> result = validator.validate(signupForm, ValidGroup2.class);

            List<String> actual = result.stream()
                    .map(v -> v.getPropertyPath().toString())
                    .collect(Collectors.toList());

            assertThat(actual).containsOnly("age");
        }

        @Test
        @DisplayName("準正常系：ageの値が最大値より大きい場合、エラーが存在すること")
        void testAgeMax() {
            SignupForm signupForm = createSignupForm();
            signupForm.setAge(101);

            Set<ConstraintViolation<SignupForm>> result = validator.validate(signupForm, ValidGroup2.class);

            List<String> actual = result.stream()
                    .map(v -> v.getPropertyPath().toString())
                    .collect(Collectors.toList());

            assertThat(actual).containsOnly("age");
        }
    }
}
