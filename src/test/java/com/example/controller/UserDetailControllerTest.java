package com.example.controller;

import com.example.domain.user.model.MUser;
import com.example.domain.user.service.UserService;
import com.example.form.UserDetailForm;
import static com.example.utils.SampleMUser.createGeneralUserA;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.modelmapper.ModelMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class UserDetailControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService mockUserService;

    @MockBean
    UserDetailsService mockUserDetailService;

    @Autowired
    private ModelMapper modelMapper;


    @Nested
    class GetUser {

        @Test
        @WithMockUser
        @Sql("classpath:testData/data.sql")
        @DisplayName("正常系: ログインした状態だと詳細画面が表示される")
        void testGetUser() throws Exception {
            MUser mUser = createGeneralUserA();
            mUser.setPassword(null);
            doReturn(mUser).when(mockUserService).getUserOne(any());
            UserDetailForm userDetailForm;
            userDetailForm = modelMapper.map(mUser, UserDetailForm.class);
            userDetailForm.setSalaryList(mUser.getSalaryList());

            mockMvc.perform(get("/user/detail/{userId}", "user@co.jp"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("userDetailForm", userDetailForm))
                    .andExpect(view().name("user/detail"));

            ArgumentCaptor<String> userDetailFormArgumentCaptor = ArgumentCaptor.forClass(String.class);
            verify(mockUserService, times(1)).getUserOne(userDetailFormArgumentCaptor.capture());
            String userDetailFormArgVal = userDetailFormArgumentCaptor.getValue();
            assertThat(userDetailFormArgVal).isEqualTo("user@co.jp");
        }

        @Test
        @DisplayName("異常系：ログインをしていない場合、ログイン画面へ戻る")
        void testGetUser1() throws Exception {
            MUser mUser = createGeneralUserA();
            mUser.setPassword(null);
            doReturn(mUser).when(mockUserService).getUserOne(any());
            UserDetailForm userDetailForm;
            userDetailForm = modelMapper.map(mUser, UserDetailForm.class);
            userDetailForm.setSalaryList(mUser.getSalaryList());

            mockMvc.perform(get("/user/detail", ""))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlTemplate("http://localhost/login"));

            verify(mockUserService, times(0)).getUserOne(any());
        }

        @Test
        @WithMockUser
        @DisplayName("異常系：DataAccessExceptionが発生した場合、error画面へ遷移する")
        void testGetUser2() throws Exception {
            doThrow(new DataAccessException("userDetailForm") {
            }).when(mockUserService).getUserOne(any());

            mockMvc.perform(get("/user/detail/{userId}", "user@co.jp"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("error", ""))
                    .andExpect(model().attribute("message", "DataAccessExceptionが発生しました"))
                    .andExpect(model().attribute("status", HttpStatus.INTERNAL_SERVER_ERROR))
                    .andExpect(view().name("error"));

            ArgumentCaptor<String> signupArgCaptor1 = ArgumentCaptor.forClass(String.class);
            verify(mockUserService, times(1)).getUserOne(signupArgCaptor1.capture());
            String userDetailFormArgVal = signupArgCaptor1.getValue();
            assertThat(userDetailFormArgVal).isEqualTo("user@co.jp");
        }


        @Nested
        class UpdateUser {

            @Test
            @WithMockUser
            @DisplayName("正常系：ログイン成功後、ユーザー更新処理後にユーザ画面へ遷移する。")
            void testUpdateUser() throws Exception {
                doNothing().when(mockUserService).updateUserOne(any(), any(), any());
                UserDetailForm userDetailForm = new UserDetailForm();

                mockMvc.perform(post("/user/detail")
                                .param("update", "")
                                .with(csrf()))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/user/list")
                        );
                ArgumentCaptor<String> deleteArgCaptor = ArgumentCaptor.forClass(String.class);
                verify(mockUserService, times(1))
                        .updateUserOne(deleteArgCaptor.capture(), deleteArgCaptor.capture(), deleteArgCaptor.capture());
                String updateArgVal = deleteArgCaptor.getValue();
                assertThat(updateArgVal).isEqualTo(userDetailForm.getUserId());
                assertThat(updateArgVal).isEqualTo(userDetailForm.getUserName());
                assertThat(updateArgVal).isEqualTo(userDetailForm.getPassword());

            }

            @Test
            @DisplayName("異常系：ログインをしていない場合、ログイン画面へ戻る")
            void testUpdateUser1() throws Exception {
                doNothing().when(mockUserService).updateUserOne(any(), any(), any());

                mockMvc.perform(post("/user/detail")
                                .param("update", "")
                                .with(csrf()))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("http://localhost/login"));

                verify(mockUserService, times(0)).updateUserOne(any(), any(), any());
            }
        }

        @Nested
        class DeleteUser {

            @Test
            @WithMockUser
            @DisplayName("正常系：ユーザ削除処理をした場合、ユーザリスト画面へ遷移する")
            void TestDeleteUser() throws Exception {
                doNothing().when(mockUserService).deleteUserOne(any());
                UserDetailForm userDetailForm = new UserDetailForm();

                mockMvc.perform(post("/user/detail")
                                .param("delete", "")
                                .with(csrf()))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/user/list"));

                ArgumentCaptor<String> deleteArgCaptor = ArgumentCaptor.forClass(String.class);
                verify(mockUserService, times(1))
                        .deleteUserOne(deleteArgCaptor.capture());
                String deleteArgVal = deleteArgCaptor.getValue();
                assertThat(deleteArgVal).isEqualTo(userDetailForm.getUserId());

            }

            @Test
            @DisplayName("異常系：ログインしていない場合、login画面へ遷移する")
            void TestDeleteUser1() throws Exception {
                doNothing().when(mockUserService).deleteUserOne(any());

                mockMvc.perform(post("/user/detail")
                                .param("delete", "")
                                .with(csrf()))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("http://localhost/login"));

                verify(mockUserService, times(0)).deleteUserOne(any());
            }

            @Test
            @WithMockUser
            @DisplayName("異常系：DataAccessExceptionが発生した場合、error画面へ遷移する")
            void TestDeleteUser2() throws Exception {
                doThrow(new DataAccessException("userDetailForm") {
                }).when(mockUserService).deleteUserOne(any());
                UserDetailForm userDetailForm = new UserDetailForm();

                mockMvc.perform(post("/user/detail")
                                .param("delete", "")
                                .with(csrf()))
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("error", ""))
                        .andExpect(model().attribute("message", "DataAccessExceptionが発生しました"))
                        .andExpect(model().attribute("status", HttpStatus.INTERNAL_SERVER_ERROR))
                        .andExpect(view().name("error")
                        );

                ArgumentCaptor<String> deleteArgCaptor = ArgumentCaptor.forClass(String.class);
                verify(mockUserService, times(1))
                        .deleteUserOne(deleteArgCaptor.capture());
                String deleteArgVal = deleteArgCaptor.getValue();
                assertThat(deleteArgVal).isEqualTo(userDetailForm.getUserId());

            }
        }
    }
}