package com.example.controller;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    UserDetailsService mockUserDetailsService;

    @Nested
    class PostAdminLogin {

        @Test
        @WithMockUser(roles ={"ADMIN"})
        @DisplayName("正常系：ADMINユーザーでログインした場合、ログインができる")
        void getAdmin() throws Exception {
            String userId = "system@co.jp";
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

            verify(mockUserDetailsService, times(1)).loadUserByUsername(eq(userId));

            mockMvc.perform(get("/admin"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/admin"));
        }
    }
}