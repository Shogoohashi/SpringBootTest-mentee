package com.example.rest;

import com.example.domain.user.model.MUser;
import com.example.domain.user.service.UserService;
import com.example.form.SignupForm;
import com.example.form.UserDetailForm;
import com.example.form.UserListForm;
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
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
@Import(ModelMapper.class)
class UserRestControllerTest {

    @MockBean
    UserService mockUserService;

    @InjectMocks
    UserRestController userRestController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Nested
    class GetUserList {
        @Test
        @WithMockUser
        @DisplayName("正常系: レスポンスにユーザー一覧が存在すること")
        void case1() throws Exception {
            MUser mUser1 = createGeneralUserA();
            MUser mUser2 = createGeneralUserB();
            List<MUser> getUsersReturnedVal = Arrays.asList(mUser1, mUser2);
            doReturn(getUsersReturnedVal).when(mockUserService).getUsers(any());
            UserListForm userListForm = new UserListForm();
            userListForm.setUserId(mUser1.getUserId());
            userListForm.setUserName(mUser1.getUserName());

            mockMvc.perform(get("/user/get/list")
                            .with(csrf())
                            .flashAttr("userListForm", userListForm))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(getUsersReturnedVal)));

            ArgumentCaptor<MUser> getUserArgumentCaptor = ArgumentCaptor.forClass(MUser.class);
            verify(mockUserService, times(1)).getUsers(getUserArgumentCaptor.capture());
            MUser getUserArgVal = getUserArgumentCaptor.getValue();
            assertThat(getUserArgVal.getUserId()).isEqualTo(mUser1.getUserId());
            assertThat(getUserArgVal.getUserName()).isEqualTo(mUser1.getUserName());
        }

        @Test
        @DisplayName("異常系:ログインしていない場合、login画面へ遷移する。")
        void testGetUserList1() throws Exception {

            mockMvc.perform(get("/user/get/list"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("http://localhost/login"));

            verify(mockUserService, times(0)).getUsers(any());
        }
    }

    @Nested
    class PostSignup {

        @Test
        @WithMockUser
        @DisplayName("正常系:レスポンスが成功した場合、ユーザ情報を登録する。")
        void testPostSignup() throws Exception {
            doNothing().when(mockUserService).signup(any());
            SignupForm signupForm = createSignupForm();
            String json = objectMapper.writeValueAsString(signupForm);

            mockMvc.perform(post("/user/signup/rest")
                            .flashAttr("signupForm", signupForm)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk());

            ArgumentCaptor<MUser> signupArgumentCaptor = ArgumentCaptor.forClass(MUser.class);
            verify(mockUserService, times(1)).signup(signupArgumentCaptor.capture());
            MUser signupArgVal = signupArgumentCaptor.getValue();
            assertThat(signupArgVal.getUserId()).isEqualTo(signupForm.getUserId());
            assertThat(signupArgVal.getUserName()).isEqualTo(signupForm.getUserName());
            assertThat(signupArgVal.getPassword()).isEqualTo(signupForm.getPassword());
            assertThat(signupArgVal.getAge()).isEqualTo(signupForm.getAge());
            assertThat(signupArgVal.getBirthday()).isEqualTo(signupForm.getBirthday());
            assertThat(signupArgVal.getGender()).isEqualTo(signupForm.getGender());

        }

        @Test
        @DisplayName("異常系:レスポンスに不正な場合、エラーメッセージを取得する。")
        void testPostSignup1() throws Exception {
            doNothing().when(mockUserService).signup(any());
            SignupForm signupForm = createSignupForm();
            signupForm.setPassword("");
            String json = objectMapper.writeValueAsString(signupForm);

            mockMvc.perform(post("/user/signup/rest")
                            .with(csrf())
                            .flashAttr("signupForm", signupForm)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk());

            verify(mockUserService, times(0)).signup(any());
        }
    }

    @Nested
    class UpdateUser {

        @Test
        @WithMockUser
        @DisplayName("正常系:レスポンスが成功した場合、ユーザ情報が更新される。")
        void testUpdateUser() throws Exception {
            doNothing().when(mockUserService).updateUserOne(any(), any(), any());
            UserDetailForm userDetailForm = new UserDetailForm();
            userDetailForm.setUserId("test@co.jp");
            userDetailForm.setPassword("testPassword");
            userDetailForm.setUserName("テストユーザ");
            String json = objectMapper.writeValueAsString(userDetailForm);

            mockMvc.perform(put("/user/update")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .flashAttr("userDetailForm", userDetailForm))
                    .andExpect(status().isOk());

            ArgumentCaptor<String> updateUserOneArgumentCaptor1 = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> updateUserOneArgumentCaptor2 = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> updateUserOneArgumentCaptor3 = ArgumentCaptor.forClass(String.class);
            verify(mockUserService, times(1))
                    .updateUserOne(updateUserOneArgumentCaptor1.capture(), updateUserOneArgumentCaptor2.capture(), updateUserOneArgumentCaptor3.capture());
            String updateUserOneArgVal1 = updateUserOneArgumentCaptor1.getValue();
            String updateUserOneArgVal2 = updateUserOneArgumentCaptor2.getValue();
            String updateUserOneArgVal3 = updateUserOneArgumentCaptor3.getValue();
            assertThat(updateUserOneArgVal1).isEqualTo(userDetailForm.getUserId());
            assertThat(updateUserOneArgVal2).isEqualTo(userDetailForm.getPassword());
            assertThat(updateUserOneArgVal3).isEqualTo(userDetailForm.getUserName());

        }

        @Test
        @DisplayName("異常系:ログインしていない場合、ログイン画面へ遷移する")
        void testUpdateUser2() throws Exception {
            UserDetailForm userDetailForm = createUserDetailForm();
            doNothing().when(mockUserService).updateUserOne(any(), any(), any());
            String json = objectMapper.writeValueAsString(userDetailForm);

            mockMvc.perform(put("/user/update")
                            .flashAttr("userDetailForm", userDetailForm)
                            .with(csrf())
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("http://localhost/login"));

            verify(mockUserService, times(0)).updateUserOne(any(), any(), any());

        }

        @Nested
        class DeleteUser {

            @Test
            @WithMockUser
            @DisplayName("異常系:ログインしていない場合、ログイン画面へ遷移する。")
            void testDeleteUser() throws Exception {
                UserDetailForm userDetailForm = createUserDetailForm();
                doNothing().when(mockUserService).deleteUserOne(any());
                String json = objectMapper.writeValueAsString(userDetailForm);

                mockMvc.perform(delete("/user/delete")
                                .flashAttr("userDetailForm", userDetailForm)
                                .with(csrf())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
                ArgumentCaptor<String> deleteUserOneArgumentCaptor = ArgumentCaptor.forClass(String.class);
                verify(mockUserService, times(1)).deleteUserOne(deleteUserOneArgumentCaptor.capture());
                String deleteUserOneArgVal = deleteUserOneArgumentCaptor.getValue();
                assertThat(deleteUserOneArgVal).isEqualTo(userDetailForm.getUserId());
            }

            @Test
            @DisplayName("異常系:ログインしていない場合、ログイン画面へ遷移する。")
            void testDeleteUser1() throws Exception {
                UserDetailForm userDetailForm = createUserDetailForm();
                doNothing().when(mockUserService).deleteUserOne(any());
                String json = objectMapper.writeValueAsString(userDetailForm);

                mockMvc.perform(delete("/user/delete")
                                .flashAttr("userDetailForm", userDetailForm)
                                .with(csrf())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isFound())
                        .andExpect(redirectedUrl("http://localhost/login"));

                verify(mockUserService, times(0)).deleteUserOne(any());
            }
        }
    }
}
