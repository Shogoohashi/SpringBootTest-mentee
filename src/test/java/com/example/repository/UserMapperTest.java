package com.example.repository;

import com.example.domain.user.model.MUser;
import static com.example.utils.SampleMUser.createAdminUser;
import static com.example.utils.SampleMUser.createGeneralUserA;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
        MUser insertOneVal = new MUser();
        insertOneVal.setUserId("test@co.jp");
        insertOneVal.setRole("ROLE_GENERAL");

        int actual = userMapper.insertOne(insertOneVal);
        MUser actualUser = userMapper.findOne(insertOneVal.getUserId());

        assertThat(actual).isEqualTo(1);
        assertThat(actualUser.getUserId()).isEqualTo(insertOneVal.getUserId());
        assertThat(actualUser.getUserName()).isEqualTo(insertOneVal.getUserName());
        assertThat(actualUser.getPassword()).isEqualTo(insertOneVal.getPassword());
        assertThat(actualUser.getAge()).isEqualTo(insertOneVal.getAge());
        assertThat(actualUser.getRole()).isEqualTo(insertOneVal.getRole());
        assertThat(actualUser.getGender()).isEqualTo(insertOneVal.getGender());
        assertThat(actualUser.getBirthday()).isEqualTo(insertOneVal.getBirthday());
    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: userIdとuserNameがnullの場合のユーザ取得件数を表示")
    void testFindMany1() {
        MUser testUser1 = createAdminUser();
        MUser testUser2 = createGeneralUserA();

        MUser findManyVal = new MUser();
        findManyVal.setUserId(null);
        findManyVal.setUserName(null);
        List<MUser> actual = userMapper.findMany(findManyVal);

        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual.get(0).getUserId()).isEqualTo(testUser1.getUserId());
        assertThat(actual.get(0).getUserName()).isEqualTo(testUser1.getUserName());
        assertThat(actual.get(0).getPassword()).isEqualTo(testUser1.getPassword());
        assertThat(actual.get(0).getAge()).isEqualTo(testUser1.getAge());
        assertThat(actual.get(0).getBirthday()).isEqualTo(testUser1.getBirthday());
        assertThat(actual.get(0).getRole()).isEqualTo(testUser1.getRole());
        assertThat(actual.get(0).getGender()).isEqualTo(testUser1.getGender());
        assertThat(actual.get(1).getUserId()).isEqualTo(testUser2.getUserId());
        assertThat(actual.get(1).getUserName()).isEqualTo(testUser2.getUserName());
        assertThat(actual.get(1).getPassword()).isEqualTo(testUser2.getPassword());
        assertThat(actual.get(1).getAge()).isEqualTo(testUser2.getAge());
        assertThat(actual.get(1).getBirthday()).isEqualTo(testUser2.getBirthday());
        assertThat(actual.get(1).getRole()).isEqualTo(testUser2.getRole());
        assertThat(actual.get(1).getGender()).isEqualTo(testUser2.getGender());
    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: userIdがNullでuserNameがnulではない場合のユーザ取得件数を表示")
    void testFindMany2() {
        MUser testUser1 = createAdminUser();
        MUser testUser2 = createGeneralUserA();

        MUser findManyVal = new MUser();
        findManyVal.setUserId(null);
        findManyVal.setUserName("テストユーザ");
        List<MUser> actual = userMapper.findMany(findManyVal);

        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual.get(0).getUserId()).isEqualTo(testUser1.getUserId());
        assertThat(actual.get(0).getUserName()).isEqualTo(testUser1.getUserName());
        assertThat(actual.get(0).getPassword()).isEqualTo(testUser1.getPassword());
        assertThat(actual.get(0).getAge()).isEqualTo(testUser1.getAge());
        assertThat(actual.get(0).getBirthday()).isEqualTo(testUser1.getBirthday());
        assertThat(actual.get(0).getRole()).isEqualTo(testUser1.getRole());
        assertThat(actual.get(0).getGender()).isEqualTo(testUser1.getGender());
        assertThat(actual.get(1).getUserId()).isEqualTo(testUser2.getUserId());
        assertThat(actual.get(1).getUserName()).isEqualTo(testUser2.getUserName());
        assertThat(actual.get(1).getPassword()).isEqualTo(testUser2.getPassword());
        assertThat(actual.get(1).getAge()).isEqualTo(testUser2.getAge());
        assertThat(actual.get(1).getBirthday()).isEqualTo(testUser2.getBirthday());
        assertThat(actual.get(1).getRole()).isEqualTo(testUser2.getRole());
        assertThat(actual.get(1).getGender()).isEqualTo(testUser2.getGender());
    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: userNameがNullでuserIdがnulではない場合のユーザ取得件数を表示")
    void testFindMany3() {
        MUser testUser1 = createAdminUser();
        MUser testUser2 = createGeneralUserA();

        MUser findManyVal = new MUser();
        findManyVal.setUserId("test@co.jp");
        findManyVal.setUserName(null);
        List<MUser> actual = userMapper.findMany(findManyVal);

        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual.get(0).getUserId()).isEqualTo(testUser1.getUserId());
        assertThat(actual.get(0).getUserName()).isEqualTo(testUser1.getUserName());
        assertThat(actual.get(0).getPassword()).isEqualTo(testUser1.getPassword());
        assertThat(actual.get(0).getAge()).isEqualTo(testUser1.getAge());
        assertThat(actual.get(0).getBirthday()).isEqualTo(testUser1.getBirthday());
        assertThat(actual.get(0).getRole()).isEqualTo(testUser1.getRole());
        assertThat(actual.get(0).getGender()).isEqualTo(testUser1.getGender());
        assertThat(actual.get(1).getUserId()).isEqualTo(testUser2.getUserId());
        assertThat(actual.get(1).getUserName()).isEqualTo(testUser2.getUserName());
        assertThat(actual.get(1).getPassword()).isEqualTo(testUser2.getPassword());
        assertThat(actual.get(1).getAge()).isEqualTo(testUser2.getAge());
        assertThat(actual.get(1).getBirthday()).isEqualTo(testUser2.getBirthday());
        assertThat(actual.get(1).getRole()).isEqualTo(testUser2.getRole());
        assertThat(actual.get(1).getGender()).isEqualTo(testUser2.getGender());
    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: userIdに紐づくm_userレコードが取得できること")
    void testFindOne() {
        MUser mUser = createGeneralUserA();
        mUser.setRole(null);

        MUser actual = userMapper.findOne(mUser.getUserId());

        assertThat(actual.getUserId()).isEqualTo(mUser.getUserId());
        assertThat(actual.getUserName()).isEqualTo(mUser.getUserName());
        assertThat(actual.getPassword()).isEqualTo(mUser.getPassword());
        assertThat(actual.getAge()).isEqualTo(mUser.getAge());
        assertThat(actual.getRole()).isEqualTo(mUser.getRole());
        assertThat(actual.getGender()).isEqualTo(mUser.getGender());
        assertThat(actual.getBirthday()).isEqualTo(mUser.getBirthday());
    }

    @Test
    @Sql("classpath:testData/data.sql")
    @DisplayName("正常系: useIdに紐づくm_userレコードを更新する。")
    void testUpdateOne() {
        MUser mUser = createGeneralUserA();
        mUser.setPassword("testPassword");
        mUser.setUserName("テストユーザ");
        mUser.setPassword("testPassword");

        userMapper.updateOne(mUser.getUserId(),mUser.getPassword(),mUser.getUserName());
        MUser actual = userMapper.findOne(mUser.getUserId());

        assertThat(actual.getUserId()).isEqualTo(mUser.getUserId());
        assertThat(actual.getUserName()).isEqualTo(mUser.getUserName());
        assertThat(actual.getPassword()).isEqualTo(mUser.getPassword());
        assertThat(actual.getAge()).isEqualTo(mUser.getAge());
        assertThat(actual.getRole()).isEqualTo(mUser.getRole());
        assertThat(actual.getGender()).isEqualTo(mUser.getGender());
        assertThat(actual.getBirthday()).isEqualTo(mUser.getBirthday());
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
    @DisplayName("正常系: useIdに紐づくloginユーザー情報を取得する。")
    void testFindLoginUser() {
        MUser mUser = createGeneralUserA();

        MUser actual = userMapper.findLoginUser(mUser.getUserId());

        assertThat(actual.getUserId()).isEqualTo(mUser.getUserId());
        assertThat(actual.getUserName()).isEqualTo(mUser.getUserName());
        assertThat(actual.getPassword()).isEqualTo(mUser.getPassword());
        assertThat(actual.getAge()).isEqualTo(mUser.getAge());
        assertThat(actual.getRole()).isEqualTo(mUser.getRole());
        assertThat(actual.getGender()).isEqualTo(mUser.getGender());
        assertThat(actual.getBirthday()).isEqualTo(mUser.getBirthday());
    }
}
