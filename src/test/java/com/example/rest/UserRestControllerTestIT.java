package com.example.rest;

import com.example.domain.user.model.MUser;
import com.example.form.SignupForm;
import com.example.form.UserDetailForm;
import com.example.repository.UserMapper;
import static com.example.utils.SampleMUser.createGeneralUserA;
import static com.example.utils.SampleMUser.createGeneralUserB;
import static com.example.utils.SampleSignupForm.createSignupForm;
import static com.example.utils.SampleUserDetailForm.createUserDetailForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
class UserRestControllerTestIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("正常系: レスポンスにユーザー一覧が存在すること")
    void case1() throws Exception {
        List<MUser> getUsersReturnedVal = Arrays.asList(createGeneralUserA(), createGeneralUserB());

        mockMvc.perform(get("/user/get/list"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(getUsersReturnedVal)));
    }

    @Nested
    class PostSignup {
        @Test
        @WithMockUser
        @Sql("classpath:testData/data.sql")
        @DisplayName("正常系:レスポンスが成功した場合、ユーザ情報を登録する。")
        void testPostSignup() throws Exception {
            SignupForm signupForm = new SignupForm();
            signupForm.setUserId("test@co.jp");
            signupForm.setUserName("テストユーザ");
            signupForm.setPassword("password");
            MUser mUser = modelMapper.map(signupForm, MUser.class);
            String json = objectMapper.writeValueAsString(mUser);

            userMapper.insertOne(mUser);

            mockMvc.perform(post("/user/signup/rest")
                            .flashAttr("signupForm", mUser)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));

            MUser actual = userMapper.findOne(mUser.getUserId());

            assertThat(actual.getUserId()).isEqualTo(mUser.getUserId());
            assertThat(actual.getUserName()).isEqualTo(mUser.getUserName());
            assertThat(actual.getPassword()).isEqualTo(mUser.getPassword());
        }

        @Test
        @WithMockUser
        @Sql("classpath:testData/data.sql")
        @DisplayName("異常系:すでに該当ユーザが登録されていた場合、レコードに登録されていない。")
        void testPostSignup1() throws Exception {
            SignupForm signupForm = createSignupForm();
            MUser mUser = modelMapper.map(signupForm, MUser.class);

            mockMvc.perform(post("/user/signup/rest")
                            .flashAttr("signupForm", mUser)
                            .with(csrf()))
                    .andExpect(status().isOk());

            MUser actual = userMapper.findOne(mUser.getUserId());

            assertThat(actual.getUserId()).isEqualTo(mUser.getUserId());
        }
    }

    @Nested
    class UpdateUser {
        @Test
        @WithMockUser
        @Sql("classpath:testData/data.sql")
        @DisplayName("正常系:リクエストが成功した場合、更新される")
        void testUpdateUser() throws Exception {
            String testUserName = "テストユーザ";
            String testPassword = "testPassword";
            UserDetailForm userDetailForm = createUserDetailForm();
            userDetailForm.setPassword(testPassword);
            userDetailForm.setUserName(testUserName);

            String json = objectMapper.writeValueAsString(userDetailForm);

            mockMvc.perform(post("/user/update")
                            .param("update", "")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .flashAttr("userDetailForm", userDetailForm))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value(0));

            userMapper.updateOne(userDetailForm.getUserId()
                    , userDetailForm.getPassword(), userDetailForm.getUserName());

            MUser actual = userMapper.findOne(userDetailForm.getUserId());

            assertThat(actual.getUserName()).isEqualTo(userDetailForm.getUserName());
            assertThat(actual.getPassword()).isEqualTo(userDetailForm.getPassword());
        }

        @Test
        @WithMockUser
        @Sql("classpath:testData/data.sql")
        @DisplayName("異常系:リクエストが失敗した場合、更新されない")
        void testUpdateUser1() throws Exception {
            MUser mUser = createGeneralUserA();

            String testUserName = "aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeeeff";
            String testPassword = "testPassword";
            UserDetailForm userDetailForm = createUserDetailForm();
            userDetailForm.setPassword(testPassword);
            userDetailForm.setUserName(testUserName);

            String json = objectMapper.writeValueAsString(userDetailForm);

            mockMvc.perform(post("/user/update")
                            .param("update", "")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .flashAttr("userDetailForm", userDetailForm))
                    .andExpect(status().isOk());

            MUser actual = userMapper.findOne(userDetailForm.getUserId());

            assertThat(actual.getUserName()).isEqualTo(mUser.getUserName());
            assertThat(actual.getPassword()).isEqualTo(mUser.getPassword());
        }

        @Test
        @DisplayName("異常系:ログインしていない場合、ログイン画面へ遷移する")
        void testUpdateUser2() throws Exception {
            UserDetailForm userDetailForm = createUserDetailForm();
            String json = objectMapper.writeValueAsString(userDetailForm);

            mockMvc.perform(put("/user/update")
                            .flashAttr("userDetailForm", userDetailForm)
                            .with(csrf())
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("http://localhost/login"));

        }
    }

    @Nested
    class DeleteUser {

        @Test
        @WithMockUser
        @DisplayName("異常系:ログインしていない場合、ログイン画面へ遷移する。")
        void testDeleteUser() throws Exception {
            UserDetailForm userDetailForm = createUserDetailForm();
            String json = objectMapper.writeValueAsString(userDetailForm);

            mockMvc.perform(delete("/user/delete")
                            .flashAttr("userDetailForm", userDetailForm)
                            .with(csrf())
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").value(0));

            MUser actual = userMapper.findLoginUser(userDetailForm.getUserId());

            assertThat(actual).isEqualTo(null);
        }

    }
}

