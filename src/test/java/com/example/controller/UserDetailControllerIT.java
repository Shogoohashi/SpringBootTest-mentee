package com.example.controller;

import com.example.domain.user.model.MUser;
import com.example.domain.user.service.UserService;
import com.example.form.UserDetailForm;
import com.example.repository.UserMapper;
import static com.example.utils.SampleUserDetailForm.createUserDetailForm;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class UserDetailControllerIT {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService mockUserService;

    @MockBean
    UserDetailsService mockUserDetailService;

    @Autowired
    UserMapper userMapper;

    @Nested
    class GetUser {
        @Test
        @Sql("classpath:testData/data.sql")
        @WithMockUser
        @DisplayName("正常系: ユーザー詳細画面に遷移すること")
        void testGetUser() throws Exception {
            UserDetailForm userDetailForm = createUserDetailForm();
            userDetailForm.setPassword(null);

            mockMvc.perform(get("/user/detail/{userId}", "user@co.jp"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("userDetailForm", userDetailForm))
                    .andExpect(view().name("user/detail"));
        }

        @Test
        @Sql("classpath:testData/data.sql")
        @WithMockUser
        @DisplayName("正常系: ユーザー詳細画面に遷移すること")
        void testGetUser1() throws Exception {
            doThrow(new DataAccessException("userDetailForm") {
            }).when(mockUserService).getUserOne(any());

            mockMvc.perform(get("/user/detail/{userId}", "user@co.jp"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("error", ""))
                    .andExpect(model().attribute("message", "DataAccessExceptionが発生しました"))
                    .andExpect(model().attribute("status", HttpStatus.INTERNAL_SERVER_ERROR))
                    .andExpect(view().name("error"));
        }
    }

    @Nested
    class updateUser {

        @Test
        @WithMockUser
        @DisplayName("正常系：ログイン成功後、ユーザー更新処理後にユーザ画面へ遷移する。")
        void testUpdateUser() throws Exception {
            String testUserId = "test@co.jp";
            String testUserName = "テストユーザ";
            String testPassword = "testPassword";
            doNothing().when(mockUserService).updateUserOne(any(), any(), any());
            UserDetailForm userDetailForm = new UserDetailForm();
            userDetailForm.setUserId(testUserId);
            userDetailForm.setPassword(testPassword);
            userDetailForm.setUserName(testUserName);

            mockMvc.perform(post("/user/detail")
                            .param("update", "")
                            .with(csrf())
                            .flashAttr("userDetailForm", userDetailForm))
                    .andExpect(status().isFound())
                    .andExpect(view().name("redirect:/user/list")
                    );

            assertThat(userDetailForm.getUserId()).isEqualTo(testUserId);
            assertThat(userDetailForm.getUserName()).isEqualTo(testUserName);
            assertThat(userDetailForm.getPassword()).isEqualTo(testPassword);
        }

        @Test
        @WithMockUser
        @Sql("classpath:testData/data.sql")
        @DisplayName("異常系:更新エラーを起こした場合、userNameとpassWordは更新されない")
        void testUpdateUser1() throws Exception {
            MUser mUser = createGeneralUserA();

            doNothing().when(mockUserService).updateUserOne(any(), any(), any());
            UserDetailForm userDetailFormVal = createUserDetailForm();
            userDetailFormVal.setPassword("testPassword");
            userDetailFormVal.setUserName("aaaaaaaaaabbbbbbbbbccccccccccddddddddddeeeeeeeeef");

            mockMvc.perform(post("/user/detail")
                            .param("update", "")
                            .with(csrf())
                            .flashAttr("userDetailForm", userDetailFormVal))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/user/list"));

            MUser actual = userMapper.findOne(mUser.getUserId());

            assertThat(actual.getUserName()).isEqualTo(mUser.getUserName());
            assertThat(actual.getPassword()).isEqualTo(mUser.getPassword());
        }

    }


    @Test
    @WithMockUser
    @DisplayName("正常系:ユーザ削除処理をした場合、ユーザリスト画面へ遷移する")
    void testDeleteUser() throws Exception {
        String testUserId = "test@co.jp";
        doNothing().when(mockUserService).deleteUserOne(any());
        UserDetailForm userDetailForm = new UserDetailForm();
        userDetailForm.setUserId(testUserId);

        mockMvc.perform(post("/user/detail")
                        .param("delete", "")
                        .with(csrf())
                        .flashAttr("userDetailForm", userDetailForm))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/user/list"));

        MUser actual = userMapper.findOne(testUserId);
        assertThat(actual).isNull();
    }
}