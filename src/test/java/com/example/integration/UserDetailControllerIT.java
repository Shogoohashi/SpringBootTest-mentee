package com.example.integration;

import com.example.form.UserDetailForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.example.utils.SampleUserDetailForm.createUserDetailForm;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class UserDetailControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Nested
    class GetUser {
        @Test
        @Sql("classpath:testData/data.sql")
        @WithMockUser
        @DisplayName("正常系: ユーザー詳細画面に遷移すること")
        void getUser() throws Exception {
            UserDetailForm userDetailForm = createUserDetailForm();
            userDetailForm.setPassword(null);

            mockMvc.perform(get("/user/detail/{userId}", "user@co.jp"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("userDetailForm", userDetailForm))
                    .andExpect(view().name("user/detail"));
        }
    }
}
