package com.example.rest;

import com.example.domain.user.model.MUser;
import com.example.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static com.example.utils.SampleMUser.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRestController.class)
@Import(ModelMapper.class)
class UserRestControllerTest {
    @MockBean
    UserService mockUserService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class GetUserList {
        @Test
        @WithMockUser
        @DisplayName("正常系: レスポンスにユーザー一覧が存在すること")
        void case1() throws Exception {
            List<MUser> getUsersReturnedVal = Arrays.asList(createGeneralUserA(), createGeneralUserB());
            doReturn(getUsersReturnedVal).when(mockUserService).getUsers(any());

            mockMvc.perform(get("/user/get/list"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(getUsersReturnedVal)));
        }
    }
}
