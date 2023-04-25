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

    @Mock
    UserServiceImpl mockUserServiceImpl;

    @Test
    @DisplayName("リクエストが成功した場合、データ登録される。")
    void testSignup() {
        int testDepartmentId = 1;
        String testRole = "ROLE_GENERAL";
        String testPassword = "password";

        doNothing().when(mockUserServiceImpl).signup(any());
        MUser signupReturnVal = createGeneralUserA();
        signupReturnVal.setDepartmentId(1);
        signupReturnVal.setRole("ROLE_GENERAL");
        signupReturnVal.setPassword("password");

        mockUserServiceImpl.signup(signupReturnVal);

        ArgumentCaptor<MUser> insertOneArgCaptor = ArgumentCaptor.forClass(MUser.class);
        verify(mockUserServiceImpl, times(1)).signup(insertOneArgCaptor.capture());
        MUser insertArgVal = insertOneArgCaptor.getValue();
        assertThat(insertArgVal.getDepartmentId()).isEqualTo(testDepartmentId);
        assertThat(insertArgVal.getRole()).isEqualTo(testRole);
        assertThat(insertArgVal.getPassword()).isEqualTo(testPassword);
    }

    @Test
    @DisplayName("リクエストが成功した場合、ユーザーを取得する。")
    void testGetUserOne() {
        MUser getUserOneReturnVal = createGeneralUserA();
        doReturn(getUserOneReturnVal).when(mockMapper).findOne(any());

        MUser actual = userServiceImpl.getUserOne(getUserOneReturnVal.getUserId());

        assertThat(actual.getUserId()).isEqualTo(getUserOneReturnVal.getUserId());

        ArgumentCaptor<String> findOneArgCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockMapper, times(1)).findOne(findOneArgCaptor.capture());
        String findOneArgVal = findOneArgCaptor.getValue();
        assertThat(findOneArgVal).isEqualTo(getUserOneReturnVal.getUserId());
    }

    @Test
    @DisplayName("リクエストが成功した場合、ユーザー1件を更新する。")
    void updateUserOne() {
        doNothing().when(mockUserServiceImpl).updateUserOne(any(),any(),any());
        MUser updateUserOneReturnVal = createGeneralUserA();
        updateUserOneReturnVal.setUserId("test@co.jp");
        updateUserOneReturnVal.setUserName("テストユーザー");
        updateUserOneReturnVal.setPassword("testPassword");

        mockUserServiceImpl.updateUserOne(updateUserOneReturnVal.getUserId()
                ,updateUserOneReturnVal.getPassword()
                ,updateUserOneReturnVal.getUserName());

        ArgumentCaptor<String> updateOneArgCaptor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> updateOneArgCaptor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> updateOneArgCaptor3 = ArgumentCaptor.forClass(String.class);
        verify(mockUserServiceImpl, times(1))
                .updateUserOne(updateOneArgCaptor1.capture(), updateOneArgCaptor2.capture(), updateOneArgCaptor3.capture());
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
        doNothing().when(mockUserServiceImpl).deleteUserOne(any());
        MUser deleteUserOneReturnVal = createGeneralUserA();

        mockUserServiceImpl.deleteUserOne(deleteUserOneReturnVal.getUserId());

        ArgumentCaptor<String> deleteOneArgCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockUserServiceImpl, times(1)).deleteUserOne(deleteOneArgCaptor.capture());
        String deleteOneArgVal = deleteOneArgCaptor.getValue();
        assertThat(deleteOneArgVal).isEqualTo(deleteUserOneReturnVal.getUserId());
    }

    @Test
    @DisplayName("リクエストが成功した場合、ログインユーザー情報を取得する。")
    void getLoginUser() {
        MUser findLoginUserReturnVal = createGeneralUserA();
        doReturn(findLoginUserReturnVal).when(mockMapper).findLoginUser(any());
        MUser actual = userServiceImpl.getLoginUser(findLoginUserReturnVal.getUserId());

        assertThat(actual.getUserId()).isEqualTo(findLoginUserReturnVal.getUserId());

        ArgumentCaptor<String> findLoginUserArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockMapper, times(1)).findLoginUser(findLoginUserArgumentCaptor.capture());
        String findLoginUserArgVal = findLoginUserArgumentCaptor.getValue();
        assertThat(findLoginUserArgVal).isEqualTo(findLoginUserReturnVal.getUserId());
    }

}