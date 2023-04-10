package com.example.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
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
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("正常系：ADMINユーザーでログインした場合、リダイレクトされる")
        void getAdmin() throws Exception {
            mockMvc.perform(get("/admin"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/admin"));
        }

        @Test
        @WithMockUser(roles = {"GENERAL"})
        @DisplayName("異常系：ADMINユーザー以外がアクセスした場合、認可されていないためエラーになる。")
        void getAdmin1() throws Exception {
            mockMvc.perform(get("/admin"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("異常系：ログインしていない場合、失敗する。")
        void getAdmin2() throws Exception {
            mockMvc.perform(get("/admin"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("http://localhost/login"));
        }
    }
}