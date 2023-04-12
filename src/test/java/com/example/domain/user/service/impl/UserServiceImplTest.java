package com.example.domain.user.service.impl;

import com.example.domain.user.model.MUser;
import com.example.repository.UserMapper;
import static com.example.utils.SampleMUser.createGeneralUserA;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserMapper mockMapper;

    @Mock
    PasswordEncoder encoder;

    @InjectMocks
    UserServiceImpl userServiceImpl;

    @Test
    void testSignup() {
        int testDepartmentId = 1;
        String testRole = "ROLE_GENERAL";

        MUser mUser = createGeneralUserA();
        mUser.setDepartmentId(1);
        mUser.setRole("ROLE_GENERAL");
        mUser.setPassword("password");
        mockMapper.insertOne(mUser);

        ArgumentCaptor<MUser> insertOneArgCaptor = ArgumentCaptor.forClass(MUser.class);
        verify(mockMapper, times(1)).insertOne(insertOneArgCaptor.capture());
        MUser insertOneArgVal = insertOneArgCaptor.getValue();
        assertThat(insertOneArgVal.getDepartmentId()).isEqualTo(testDepartmentId);
        assertThat(insertOneArgVal.getRole()).isEqualTo(testRole);
        assertThat(insertOneArgVal.getPassword()).isEqualTo(null);
    }

    @Test
    void getUserOne() {
        MUser getUserOneReturnVal = createGeneralUserA();
        doReturn(getUserOneReturnVal).when(mockMapper).findOne(any());

        MUser mUser = createGeneralUserA();
        MUser actual = userServiceImpl.getUserOne(mUser.getUserId());

        assertThat(actual.getUserId()).isEqualTo(mUser.getUserId());

        ArgumentCaptor<String> findOneArgCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockMapper, times(1)).findOne(findOneArgCaptor.capture());
        String findOneArgVal = findOneArgCaptor.getValue();
        assertThat(findOneArgVal).isEqualTo(mUser.getUserId());
    }

    @Test
    void updateUserOne() {
        MUser mUser = createGeneralUserA();

        mockMapper.updateOne(mUser.getUserId()
                , mUser.getPassword()
                , mUser.getUserName());

        ArgumentCaptor<String> updateOneArgCaptor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> updateOneArgCaptor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> updateOneArgCaptor3 = ArgumentCaptor.forClass(String.class);
        verify(mockMapper, times(1)).updateOne(updateOneArgCaptor1.capture(), any(), any());
        verify(mockMapper, times(1)).updateOne(any(), updateOneArgCaptor2.capture(), any());
        verify(mockMapper, times(1)).updateOne(any(), any(), updateOneArgCaptor3.capture());
        String updateOneArgVal1 = updateOneArgCaptor1.getValue();
        String updateOneArgVal2 = updateOneArgCaptor2.getValue();
        String updateOneArgVal3 = updateOneArgCaptor3.getValue();
        assertThat(updateOneArgVal1).isEqualTo(mUser.getUserId());
        assertThat(updateOneArgVal2).isEqualTo(mUser.getPassword());
        assertThat(updateOneArgVal3).isEqualTo(mUser.getUserName());
    }

    @Test
    void deleteUserOne() {
        MUser mUser = createGeneralUserA();

        int testCount = mockMapper.deleteOne(mUser.getUserId());
        System.out.println(testCount);

        ArgumentCaptor<String> deleteOneArgCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockMapper, times(1)).deleteOne(deleteOneArgCaptor.capture());
        String deleteOneArgVal = deleteOneArgCaptor.getValue();
        assertThat(deleteOneArgVal).isEqualTo(mUser.getUserId());
    }

    @Test
    void getLoginUser() {
        MUser findLoginUserReturnVal = createGeneralUserA();
        doReturn(findLoginUserReturnVal).when(mockMapper).findLoginUser(any());
        MUser mUser = createGeneralUserA();
        MUser actual = userServiceImpl.getLoginUser(mUser.getUserId());

        assertThat(actual.getUserId()).isEqualTo(findLoginUserReturnVal.getUserId());

        ArgumentCaptor<String> findLoginUserArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockMapper, times(1)).findLoginUser(findLoginUserArgumentCaptor.capture());
        String findLoginUserArgVal = findLoginUserArgumentCaptor.getValue();
        assertThat(findLoginUserArgVal).isEqualTo(mUser.getUserId());
    }

}