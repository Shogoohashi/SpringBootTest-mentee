package com.example.domain.user.service.impl;

import com.example.domain.user.model.MUser;
import com.example.repository.UserMapper;
import static com.example.utils.SampleMUser.createGeneralUserA;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserMapper mockMapper;

    @InjectMocks
    UserServiceImpl userServiceImpl;


    @Test
    @DisplayName("リクエストが成功した場合、データ登録される。")
    void testSignup() {
        MUser testUser = createGeneralUserA();
        testUser.setDepartmentId(1);
        testUser.setRole("ROLE_GENERAL");
        testUser.setPassword("password");
        doReturn(1).when(mockMapper).insertOne(any());

        userServiceImpl.signup(testUser);

        ArgumentCaptor<MUser> insertOneArgCaptor = ArgumentCaptor.forClass(MUser.class);
        verify(mockMapper, times(1)).insertOne(insertOneArgCaptor.capture());
        MUser insertArgVal = insertOneArgCaptor.getValue();
        assertThat(insertArgVal.getUserId()).isEqualTo(testUser.getUserId());
        assertThat(insertArgVal.getUserName()).isEqualTo(testUser.getUserName());
        assertThat(insertArgVal.getPassword()).isEqualTo(testUser.getPassword());
        assertThat(insertArgVal.getAge()).isEqualTo(testUser.getAge());
        assertThat(insertArgVal.getBirthday()).isEqualTo(testUser.getBirthday());
        assertThat(insertArgVal.getGender()).isEqualTo(testUser.getGender());

    }

    @Test
    @DisplayName("リクエストが成功した場合、ユーザーを取得する。")
    void testGetUserOne() {
        String testUser = "user@co.jp";

        MUser getUserOneReturnVal = createGeneralUserA();
        doReturn(getUserOneReturnVal).when(mockMapper).findOne(any());

        MUser actual = userServiceImpl.getUserOne(getUserOneReturnVal.getUserId());
        assertThat(actual.getUserId()).isEqualTo(getUserOneReturnVal.getUserId());
        assertThat(actual.getUserName()).isEqualTo(getUserOneReturnVal.getUserName());
        assertThat(actual.getPassword()).isEqualTo(getUserOneReturnVal.getPassword());
        assertThat(actual.getAge()).isEqualTo(getUserOneReturnVal.getAge());
        assertThat(actual.getRole()).isEqualTo(getUserOneReturnVal.getRole());
        assertThat(actual.getBirthday()).isEqualTo(getUserOneReturnVal.getBirthday());
        assertThat(actual.getGender()).isEqualTo(getUserOneReturnVal.getGender());

        ArgumentCaptor<String> findOneArgCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockMapper, times(1)).findOne(findOneArgCaptor.capture());
        String findOneArgVal = findOneArgCaptor.getValue();
        assertThat(findOneArgVal).isEqualTo(testUser);
    }

    @Test
    @DisplayName("リクエストが成功した場合、ユーザー1件を更新する。")
    void updateUserOne() {
        doNothing().when(mockMapper).updateOne(any(),any(),any());
        MUser updateUserOneReturnVal = createGeneralUserA();
        updateUserOneReturnVal.setUserId("test@co.jp");
        updateUserOneReturnVal.setUserName("テストユーザー");
        updateUserOneReturnVal.setPassword("testPassword");

        userServiceImpl.updateUserOne(updateUserOneReturnVal.getUserId()
                ,updateUserOneReturnVal.getPassword()
                ,updateUserOneReturnVal.getUserName());

        ArgumentCaptor<String> updateOneArgCaptor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> updateOneArgCaptor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> updateOneArgCaptor3 = ArgumentCaptor.forClass(String.class);
        verify(mockMapper, times(1))
                .updateOne(updateOneArgCaptor1.capture(), updateOneArgCaptor2.capture(), updateOneArgCaptor3.capture());
        String updateOneArgVal1 = updateOneArgCaptor1.getValue();
        String updateOneArgVal2 = updateOneArgCaptor2.getValue();
        String updateOneArgVal3 = updateOneArgCaptor3.getValue();
        assertThat(updateOneArgVal1).isEqualTo(updateUserOneReturnVal.getUserId());
        assertThat(updateOneArgVal2).isEqualTo(updateUserOneReturnVal.getPassword());
        assertThat(updateOneArgVal3).isEqualTo(updateUserOneReturnVal.getUserName());
    }

    @Test
    @DisplayName("リクエストが成功した場合、ユーザー1件を削除する。")
    void deleteUserOne() {
        MUser deleteUserOneReturnVal = createGeneralUserA();
        doReturn(1).when(mockMapper).deleteOne(any());

        userServiceImpl.deleteUserOne(deleteUserOneReturnVal.getUserId());

        ArgumentCaptor<String> deleteOneArgCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockMapper, times(1)).deleteOne(deleteOneArgCaptor.capture());
        String deleteOneArgVal = deleteOneArgCaptor.getValue();
        assertThat(deleteOneArgVal).isEqualTo(deleteUserOneReturnVal.getUserId());
    }

    @Test
    @DisplayName("リクエストが成功した場合、ログインユーザー情報を取得する。")
    void getLoginUser() {
        String testUser = "user@co.jp";

        MUser findLoginUserReturnVal = createGeneralUserA();
        doReturn(findLoginUserReturnVal).when(mockMapper).findLoginUser(any());
        MUser actual = userServiceImpl.getLoginUser(findLoginUserReturnVal.getUserId());

        assertThat(actual.getUserId()).isEqualTo(findLoginUserReturnVal.getUserId());

        ArgumentCaptor<String> findLoginUserArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockMapper, times(1)).findLoginUser(findLoginUserArgumentCaptor.capture());
        String findLoginUserArgVal = findLoginUserArgumentCaptor.getValue();
        assertThat(findLoginUserArgVal).isEqualTo(testUser);
    }

}