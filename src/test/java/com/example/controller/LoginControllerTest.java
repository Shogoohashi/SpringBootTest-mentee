package com.example.controller;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(LoginController.class)
class LoginControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    UserDetailsService mockUserDetailsService;

    @Test
    @DisplayName("loginのリクエストが成功した場合、login画面へ遷移する")
    void getLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/login"));
    }

    @Nested
    class PostLogin {
        @Test
        @DisplayName("正常系: ログインができ、ユーザー一覧画面にリダイレクトすること")
        void case1() throws Exception {
            String userId = "user@co.jp";
            String password = "password";
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
            UserDetails loadUserByUsernameReturnedVal = new User(userId, passwordEncoder.encode(password), authorities);
            doReturn(loadUserByUsernameReturnedVal).when(mockUserDetailsService).loadUserByUsername(anyString());

            mockMvc.perform(formLogin()
                            .loginProcessingUrl("/login")
                            .user("userId", userId)
                            .password("password", password)
                    )
                    .andExpect(authenticated())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/user/list"));

            verify(mockUserDetailsService, times(1)).loadUserByUsername(eq(userId)); // 引数がプリミティブの場合はeqメソッドを使って検証が可能(オブジェクトの場合は不可)
        }

        @Test
        @DisplayName("異常系: 該当ユーザが存在しない")
        void case2() throws Exception {
            String userId = "ohashi";
            String password = "password";
            doThrow(new UsernameNotFoundException("user not found")).when(mockUserDetailsService).loadUserByUsername(anyString());

            mockMvc.perform(formLogin()
                            .loginProcessingUrl("/login")
                            .user("userId", userId)
                            .password("password", password)
                    )
                    .andExpect(unauthenticated())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/login?error"));

            verify(mockUserDetailsService, times(1)).loadUserByUsername(eq(userId));
        }
    }
}
