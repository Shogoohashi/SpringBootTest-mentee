package com.example.controller;

import com.example.domain.user.model.MUser;
import com.example.domain.user.service.UserService;
import com.example.form.UserListForm;
import static com.example.utils.SampleMUser.createGeneralUserA;
import static com.example.utils.SampleMUser.createGeneralUserB;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(UserListController.class)
@Import(ModelMapper.class)
class UserListControllerTest {

    @MockBean
    UserService mockUserService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MockMvc mockMvc;

    @Nested
    class GetUser {

        @Test
        @WithMockUser
        @DisplayName("正常系：ログイン状態だとユーザ一覧画面を表示される。")
        void testGetUserList() throws Exception {
            MUser test = new MUser();
            test.setUserId(null);
            test.setUserName(null);

            MUser mUserA = createGeneralUserA();
            MUser mUserB = createGeneralUserB();
            List<MUser> userListForm = Arrays.asList(mUserA, mUserB);
            doReturn(userListForm).when(mockUserService).getUsers(any());

            mockMvc.perform(get("/user/list")
                            .flashAttr("userList", userListForm))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("userList", userListForm))
                    .andExpect(view().name("user/list"));

            ArgumentCaptor<MUser> userListArgumentCaptor = ArgumentCaptor.forClass(MUser.class);
            verify(mockUserService, times(1)).getUsers(userListArgumentCaptor.capture());
            MUser userListArgVal = userListArgumentCaptor.getValue();
            assertThat(userListArgVal.getUserId()).isEqualTo(test.getUserId());
            assertThat(userListArgVal.getUserName()).isEqualTo(test.getUserName());
        }

        @Test
        @DisplayName("異常系：ログインをしていない場合、ログイン画面へ戻る")
        void testGetUserList1() throws Exception {
            mockMvc.perform(get("/user/list"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("http://localhost/login"));

            verify(mockUserService, times(0)).getUsers(any());

        }

        @Test
        @WithMockUser
        @DisplayName("異常系：DataAccessExceptionが発生した場合、error画面へ遷移する")
        void testGetUserList2() throws Exception {
            doThrow(new DataAccessException("userList") {
            }).when(mockUserService).getUsers(any());
            UserListForm userListForm = new UserListForm();
            userListForm.setUserId(null);
            userListForm.setUserName(null);

            mockMvc.perform(get("/user/list"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("error", ""))
                    .andExpect(model().attribute("message", "DataAccessExceptionが発生しました"))
                    .andExpect(model().attribute("status", HttpStatus.INTERNAL_SERVER_ERROR))
                    .andExpect(view().name("error"));

            ArgumentCaptor<MUser> userListArgumentCaptor = ArgumentCaptor.forClass(MUser.class);
            verify(mockUserService, times(1)).getUsers(userListArgumentCaptor.capture());
            MUser userListArgVal = userListArgumentCaptor.getValue();
            assertEquals(userListArgVal.getUserId(), userListForm.getUserId());
            assertEquals(userListArgVal.getUserName(), userListForm.getUserName());
        }
    }

    @Nested
    class PostUserList {


        @Test
        @WithMockUser
        @DisplayName("正常系：ログイン状態でユーザ画面へ遷移した場合、ユーザ検索処理がされる。")
        void testPostUserList() throws Exception {
            MUser mUserA = createGeneralUserA();
            MUser mUserB = createGeneralUserB();
            List<MUser> mUserList = Arrays.asList(mUserA, mUserB);
            doReturn(mUserList).when(mockUserService).getUsers(any());
            UserListForm userListForm;
            userListForm = modelMapper.map(mUserList, UserListForm.class);

            mockMvc.perform(post("/user/list")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("userListForm", userListForm))
                    .andExpect(view().name("user/list"));

            ArgumentCaptor<MUser> userListArgumentCaptor = ArgumentCaptor.forClass(MUser.class);
            verify(mockUserService, times(1)).getUsers(userListArgumentCaptor.capture());
            MUser userListArgVal = userListArgumentCaptor.getValue();
            assertEquals(userListArgVal.getUserId(), userListForm.getUserId());
            assertEquals(userListArgVal.getUserName(), userListForm.getUserName());
        }

        @Test
        @DisplayName("異常系：ログインをしていない場合、ログイン画面へ戻る")
        void testPostUserList1() throws Exception {
            mockMvc.perform(post("/user/list")
                            .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("http://localhost/login"));

            verify(mockUserService, times(0)).getUsers(any());

        }

        @Test
        @WithMockUser
        @DisplayName("異常系：DataAccessExceptionが発生した場合、error画面へ遷移する")
        void testPostUserList2() throws Exception {
            doThrow(new DataAccessException("userList") {
            }).when(mockUserService).getUsers(any());
            UserListForm userListForm = new UserListForm();
            userListForm.setUserId(null);
            userListForm.setUserName(null);

            mockMvc.perform(post("/user/list")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("error", ""))
                    .andExpect(model().attribute("message", "DataAccessExceptionが発生しました"))
                    .andExpect(model().attribute("status", HttpStatus.INTERNAL_SERVER_ERROR))
                    .andExpect(view().name("error"));

            ArgumentCaptor<MUser> userListArgumentCaptor = ArgumentCaptor.forClass(MUser.class);
            verify(mockUserService, times(1)).getUsers(userListArgumentCaptor.capture());
            MUser userListArgVal = userListArgumentCaptor.getValue();
            assertEquals(userListArgVal.getUserId(), userListForm.getUserId());
            assertEquals(userListArgVal.getUserName(), userListForm.getUserName());
        }
    }

}