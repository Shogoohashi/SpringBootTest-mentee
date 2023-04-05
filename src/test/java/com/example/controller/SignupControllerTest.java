package com.example.controller;

import com.example.application.service.UserApplicationService;
import com.example.domain.user.model.MUser;
import com.example.domain.user.service.UserService;
import com.example.form.SignupForm;
import static com.example.utils.SampleSignupForm.createSignupForm;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.web.bind.annotation.ExceptionHandler;

@WebMvcTest(SignupController.class)
@Import(ModelMapper.class)
class SignupControllerTest {

    @MockBean
    UserApplicationService mockUserApplicationService;

    @MockBean
    UserService mockUserService;

    @Mock
    MessageSource messageSource;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("正常系:getSignupのリクエストが成功すること。")
    void getSignup() throws Exception {
        Map<String, Integer> genderMap = new HashMap<>();
        doReturn(genderMap).when(mockUserApplicationService).getGenderMap(any(Locale.class));

        mockMvc.perform(get("/user/signup")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
//                .andExpect(model().attribute(, 5))
                .andExpect(view().name("user/signup"));

        ArgumentCaptor<MUser> signupArgCaptor1 = ArgumentCaptor.forClass(MUser.class);
        verify(mockUserService, times(1)).signup(signupArgCaptor1.capture());
    }

    @Nested
    class PostSignup {
        @Test
        @DisplayName("正常系: UserService#signupを呼ぶこと, ログイン画面にリダイレクトすること")
        void case1() throws Exception {
            doNothing().when(mockUserService).signup(any());

            SignupForm signupForm = createSignupForm();
            mockMvc.perform(post("/user/signup")
                            .with(csrf())
                            .flashAttr("signupForm", signupForm)
                    )
                    .andExpect(status().isFound())
                    .andExpect(model().hasNoErrors())
                    .andExpect(redirectedUrl("/login"));

            ArgumentCaptor<MUser> signupArgCaptor1 = ArgumentCaptor.forClass(MUser.class);
            verify(mockUserService, times(1)).signup(signupArgCaptor1.capture());
            MUser signupArgVal1 = signupArgCaptor1.getValue();
            assertEquals(signupArgVal1.getUserId(), signupForm.getUserId());
            assertEquals(signupArgVal1.getPassword(), signupForm.getPassword());
            assertEquals(signupArgVal1.getUserName(), signupForm.getUserName());
            assertEquals(signupArgVal1.getBirthday(), signupForm.getBirthday());
            assertEquals(signupArgVal1.getAge(), signupForm.getAge());
            assertEquals(signupArgVal1.getGender(), signupForm.getGender());
            // テストライブラリのAssertJを使う場合は下記の様に書く
            assertThat(signupArgVal1.getUserId()).isEqualTo(signupForm.getUserId());
            assertThat(signupArgVal1.getPassword()).isEqualTo(signupForm.getPassword());
            assertThat(signupArgVal1.getUserName()).isEqualTo(signupForm.getUserName());
            assertThat(signupArgVal1.getBirthday()).isEqualTo(signupForm.getBirthday());
            assertThat(signupArgVal1.getAge()).isEqualTo(signupForm.getAge());
            assertThat(signupArgVal1.getGender()).isEqualTo(signupForm.getGender());
            // オブジェクトのフィールドを一括で検証したい場合は下記の様にも書ける(https://assertj.github.io/doc/#basic-usage)
            assertThat(signupArgVal1).usingRecursiveComparison()
                    .ignoringFields("departmentId", "role", "department", "salaryList")
                    .isEqualTo(signupForm);
        }

        @Test
        @DisplayName("異常系:リクエストが失敗した場合、バリデーションチェックエラーが発生する。")
        void dataAccessExceptionHandler() throws Exception {
            doThrow(new UsernameNotFoundException("message")).when(mockUserService).signup(any());

            SignupForm signupForm = createSignupForm();
            mockMvc.perform(post("/user/signup").param("message", "SignupControllerで例外が発生しました")
                            .with(csrf())
                            .flashAttr("signupForm", signupForm)
                    )
                    .andExpect(model().hasNoErrors())
                    .andExpect(model().attribute("message", "SignupControllerで例外が発生しました"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("error"));

            ArgumentCaptor<MUser> signupArgCaptor1 = ArgumentCaptor.forClass(MUser.class);
            verify(mockUserService, times(1)).signup(signupArgCaptor1.capture());
        }

        @Test
        @ExceptionHandler(DataAccessException.class)
        void exceptionHandler() throws Exception {
            doThrow(new UsernameNotFoundException("message")).when(mockUserService).signup(any());

            SignupForm signupForm = createSignupForm();
            mockMvc.perform(post("/user/signup")
                            .with(csrf())
                            .flashAttr("signupForm", signupForm)
                    )
                    .andExpect(model().hasNoErrors())
                    .andExpect(model().attribute("message", "SignupControllerで例外が発生しました"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("error"));

            ArgumentCaptor<MUser> signupArgCaptor1 = ArgumentCaptor.forClass(MUser.class);
            verify(mockUserService, times(1)).signup(signupArgCaptor1.capture());
        }
    }
}
