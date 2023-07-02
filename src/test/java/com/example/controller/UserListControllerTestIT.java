package com.example.controller;

import com.example.domain.user.model.MUser;
import com.example.form.UserListForm;
import static com.example.utils.SampleMUser.createGeneralUserA;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
class UserListControllerTestIT {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("classpath:testData/data.sql")
    @WithMockUser
    void getUserList() throws Exception {
        MUser mUser1 = createGeneralUserA();
        UserListForm userListForm = new UserListForm();
        userListForm.setUserId(mUser1.getUserId());
        userListForm.setUserName(mUser1.getUserName());

        mockMvc.perform(get("/user/list")
                        .with(csrf())
                        .flashAttr("userListForm", userListForm))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userListForm", userListForm))
                .andExpect(view().name("user/list"));

    }

    @Test
    @Sql("classpath:testData/data.sql")
    @WithMockUser
    void postUserList() throws Exception {
        MUser mUserA = createGeneralUserA();
        UserListForm userListForm = new UserListForm();
        userListForm.setUserId(mUserA.getUserId());
        userListForm.setUserName(mUserA.getUserName());

        mockMvc.perform(post("/user/list")
                        .with(csrf())
                        .flashAttr("userListForm", userListForm))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userListForm", userListForm))
                .andExpect(view().name("user/list"));

    }
}