package com.example.controller;

import com.example.domain.user.model.MUser;
import com.example.domain.user.service.UserService;
import com.example.form.UserListForm;
import static com.example.utils.SampleMUser.createGeneralUserA;
import static com.example.utils.SampleMUser.createGeneralUserB;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class UserListControllerTest {

    @MockBean
    UserService mockUserService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("classpath:testData/data.sql")
    @WithMockUser
    void getUserList() throws Exception {
        MUser test = new MUser();
        test.setUserId(null);
        test.setUserName(null);

        MUser mUser1 = createGeneralUserA();
        MUser mUser2 = createGeneralUserB();
        List<MUser> mUserList = Arrays.asList(mUser1, mUser2);
        doReturn(mUserList).when(mockUserService).getUsers(any());
        UserListForm userListForm = new UserListForm();
        userListForm.setUserId(mUser1.getUserId());
        userListForm.setUserName(mUser1.getUserName());

        mockMvc.perform(get("/user/list")
                        .flashAttr("UserListForm", userListForm))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userList", mUserList))
                .andExpect(view().name("user/list"));

    }

    @Test
    @Sql("classpath:testData/data.sql")
    @WithMockUser
    void postUserList() throws Exception{
        MUser test = new MUser();
        test.setUserId(null);
        test.setUserName(null);

        MUser mUserA = createGeneralUserA();
        MUser mUserB = createGeneralUserB();
        List<MUser> mUserList = Arrays.asList(mUserA, mUserB);
        doReturn(mUserList).when(mockUserService).getUsers(any());
        UserListForm userListForm = new UserListForm();
        userListForm.setUserId(mUserA.getUserId());
        userListForm.setUserName(mUserA.getUserName());

        mockMvc.perform(post("/user/list")
                        .with(csrf())
                        .flashAttr("UserListForm", userListForm))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userList", mUserList))
                .andExpect(view().name("user/list"));

    }
}