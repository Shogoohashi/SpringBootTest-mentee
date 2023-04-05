package com.example.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogoutController.class)
class LogoutControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Nested
    class PostSignup {

        @Test
        @DisplayName("正常系: ログイン状態でログアウトができる。")
        @WithMockUser(roles = {"GENERAL"})
        void postLogout() throws Exception {
            mockMvc.perform(logout("/logout"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/login?logout")
                    );
        }
    }
}