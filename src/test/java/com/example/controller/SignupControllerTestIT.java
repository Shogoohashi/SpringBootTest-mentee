package com.example.controller;

import com.example.application.service.UserApplicationService;
import com.example.domain.user.model.MUser;
import com.example.domain.user.service.UserService;
import com.example.form.SignupForm;
import com.example.repository.UserMapper;
import static com.example.utils.SampleMUser.createGeneralUserA;
import static com.example.utils.SampleSignupForm.createSignupForm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
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
class SignupControllerTestIT {


    @MockBean
    UserApplicationService mockUserApplicationService;

    @SpyBean
    SignupController mockSignupController;

    @MockBean
    UserService mockUserService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserMapper userMapper;

    @Test
    @DisplayName("正常系:getSignupのリクエストが成功すること。")
    void getSignup() throws Exception {
        Map<String, Integer> genderMap = new HashMap<>();
        genderMap.put("male", 1);
        genderMap.put("female", 2);
        doReturn(genderMap).when(mockUserApplicationService).getGenderMap(any());

        mockMvc.perform(get("/user/signup"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("genderMap", genderMap))
                .andExpect(view().name("user/signup"));

        verify(mockUserApplicationService, times(1)).getGenderMap(any());
    }


    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: UserService#signupを呼ぶこと, ログイン画面にリダイレクトすること")
    void case1() throws Exception {
        doNothing().when(mockUserService).signup(any());
        SignupForm testUser = createSignupForm();
        testUser.setUserId("test@co.jp");

        SignupForm signupForm = createSignupForm();
        signupForm.setUserId("test@co.jp");

        mockMvc.perform(post("/user/signup")
                        .with(csrf())
                        .flashAttr("signupForm", signupForm)
                )
                .andExpect(status().isFound())
                .andExpect(model().hasNoErrors())
                .andExpect(redirectedUrl("/login"));

        assertThat(signupForm).isEqualTo(testUser);
    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: UserService#signupを呼ぶこと, ログイン画面にリダイレクトすること")
    void case2() throws Exception {
        doThrow(new DataAccessException("") {
        }).when(mockUserService).signup(any());
        MUser expected = createGeneralUserA();
        SignupForm signupForm = createSignupForm();

        mockMvc.perform(post("/user/signup")
                        .with(csrf())
                        .flashAttr("signupForm", signupForm)
                )
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", ""))
                .andExpect(model().attribute("message", "SignupControllerで例外が発生しました"))
                .andExpect(model().attribute("status", HttpStatus.INTERNAL_SERVER_ERROR))
                .andExpect(view().name("error"));

        List<MUser> actual = userMapper.findMany(expected);
        assertThat(actual.size()).isEqualTo(1);
    }
}