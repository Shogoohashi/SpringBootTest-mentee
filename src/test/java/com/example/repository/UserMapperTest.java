package com.example.repository;

import com.example.domain.user.model.MUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static com.example.utils.SampleMUser.createGeneralUserA;
import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
class UserMapperTest {
    @Autowired
    UserMapper userMapper;

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: userIdに紐づくm_userレコードが取得できること")
    void findOne() {
        MUser expected = createGeneralUserA();
        expected.setRole(null);

        MUser actual = userMapper.findOne(expected.getUserId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
