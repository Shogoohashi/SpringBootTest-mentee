package com.example.repository;

import com.example.domain.user.model.MUser;
import com.example.form.SignupForm;
import static com.example.utils.SampleMUser.createGeneralUserA;
import static com.example.utils.SampleSignupForm.createSignupForm;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doReturn;
import org.modelmapper.ModelMapper;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@Import(ModelMapper.class)
class UserMapperTest {

    @Autowired
    UserMapper userMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    @DisplayName("正常系：新規登録情報が登録されること")
    void testInsertOne() {
        MUser insertOneVal = createGeneralUserA();
        insertOneVal.setDepartmentId(1);
        insertOneVal.setRole("ROLE_GENERAL");

        int actual = userMapper.insertOne(insertOneVal);

        assertThat(actual).isEqualTo(1);

    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: userIdとuserNameがnullの場合のユーザ取得件数を表示")
    void testFindMany1() {
        SignupForm signupForm = createSignupForm();
        signupForm.setUserId(null);
        signupForm.setUserName(null);
        MUser expected = modelMapper.map(signupForm, MUser.class);

        List<MUser> actual = userMapper.findMany(expected);

        assertThat(actual.size()).isEqualTo(2);
    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: userIdがNullでuserNameがnulではない場合のユーザ取得件数を表示")
    void testFindMany2() {
        SignupForm signupForm = createSignupForm();
        signupForm.setUserId(null);
        MUser expected = modelMapper.map(signupForm, MUser.class);

        List<MUser> actual = userMapper.findMany(expected);

        assertThat(actual.size()).isEqualTo(1);
    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: userNameがNullでuserIdがnulではない場合のユーザ取得件数を表示")
    void testFindMany3() {
        SignupForm signupForm = createSignupForm();
        signupForm.setUserName(null);
        MUser expected = modelMapper.map(signupForm, MUser.class);

        List<MUser> actual = userMapper.findMany(expected);

        assertThat(actual.size()).isEqualTo(1);
    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: userIdに紐づくm_userレコードが取得できること")
    void testFindOne() {
        MUser expected = createGeneralUserA();
        expected.setRole(null);

        MUser actual = userMapper.findOne(expected.getUserId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: useIdに紐づくm_userレコードを更新する。")
    void testUpdateOne() {
        String testPassword = "testPassword";
        String testUserName = "テストユーザ";
        MUser mUser = createGeneralUserA();
        mUser.setPassword("testPassword");
        mUser.setUserName("テストユーザ");

        userMapper.updateOne(mUser.getUserId(),mUser.getPassword(),mUser.getUserName());
        MUser actual = userMapper.findOne(mUser.getUserId());

        assertThat(actual.getPassword()).isEqualTo(testPassword);
        assertThat(actual.getUserName()).isEqualTo(testUserName);
    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: useIdに紐づくm_userレコードを削除する。")
    void testDeleteOne() {
        MUser mUser = createGeneralUserA();

        userMapper.deleteOne(mUser.getUserId());
        MUser actual = userMapper.findOne(mUser.getUserId());

        assertThat(actual).isNull();
    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: useIdに紐づくm_userレコードを削除する。")
    void testFindLoginUser() {
        MUser signupForm = createGeneralUserA();
        signupForm.setDepartment(null);
        signupForm.setSalaryList(null);
        MUser expected = modelMapper.map(signupForm, MUser.class);

        MUser actual = userMapper.findLoginUser(expected.getUserId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
