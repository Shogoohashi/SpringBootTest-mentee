package com.example.controller;

import com.example.application.service.UserApplicationService;
import com.example.domain.user.model.MUser;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SignupControllerTestIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserApplicationService userApplicationService;

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

        mockMvc.perform(get("/user/signup"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("user/signup"));
    }


    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: UserService#signupを呼ぶこと, ログイン画面にリダイレクトすること")
    void case1() throws Exception {
        String userIdTest = "testUser@co.jp";

        SignupForm signupForm = createSignupForm();
        signupForm.setUserId(userIdTest);

        mockMvc.perform(post("/user/signup")
                        .with(csrf())
                        .flashAttr("signupForm", signupForm)
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));

        MUser actual = userMapper.findOne(signupForm.getUserId());
        assertThat(actual.getUserId()).isEqualTo(signupForm.getUserId());
    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("異常系:該当ユーザーが既に登録されていた場合、エラーが出る")
    void case2() throws Exception {
        SignupForm signupForm = createSignupForm();
        MUser mUser = createGeneralUserA();

        mockMvc.perform(post("/user/signup")
                        .with(csrf())
                        .flashAttr("signupForm", signupForm)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        List<MUser> actual = userMapper.findMany(mUser);
        assertThat(actual.size()).isEqualTo(1);
    }
}