package com.example.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
class LoginControllerTestIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserDetailsService userDetailsService;


    @Test
    @DisplayName("loginのリクエストが成功した場合、login画面へ遷移する")
    void getLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/login"));
    }

    @Test
    @DisplayName("正常系: ログインができ、ユーザー一覧画面にリダイレクトすること")
    @Sql("classpath:testData/data.sql")
    void case1() throws Exception {
        String userId = "user@co.jp";
        String password = "password";

        mockMvc.perform(formLogin()
                        .loginProcessingUrl("/login")
                        .user("userId", userId)
                        .password("password", password)
                )
                .andExpect(status().isFound())
                .andExpect(authenticated().withUsername(userId))
                .andExpect(redirectedUrl("/user/list"));
    }

    @Test
    @DisplayName("異常系: 該当ユーザが存在しない場合、ログインエラー")
    void case2() throws Exception {
        String userId = "ohashi";
        String password = "password";

        mockMvc.perform(formLogin()
                        .loginProcessingUrl("/login")
                        .user("userId", userId)
                        .password("password", password)
                )
                .andExpect(unauthenticated())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error"));

    }

    @Test
    @DisplayName("異常系：パスワードが違う場合、ログインエラー")
    void case3() throws Exception {
        String userId = "user@co.jp";
        String password = "password";

        mockMvc.perform(formLogin()
                        .loginProcessingUrl("/login")
                        .user("userId", userId)
                        .password("password", "testWord")
                )
                .andExpect(unauthenticated())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error"));
    }
}