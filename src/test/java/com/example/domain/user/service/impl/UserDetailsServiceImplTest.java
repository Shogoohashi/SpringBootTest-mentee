package com.example.domain.user.service.impl;

import com.example.domain.user.model.MUser;
import com.example.domain.user.service.UserService;
import static com.example.utils.SampleMUser.createGeneralUserA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    UserService mockUserService;

    @InjectMocks
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Test
    @DisplayName("該当ユーザが存在した場合、該当するユーザを取得する")
    void testLoadUserByUsername() {
        MUser LoginUserReturnVal = createGeneralUserA();
        doReturn(LoginUserReturnVal).when(mockUserService).getLoginUser(any());

        MUser mUser = createGeneralUserA();
        UserDetails actual = userDetailsServiceImpl.loadUserByUsername("ユーザー1");

        assertThat(actual.getUsername()).isEqualTo(LoginUserReturnVal.getUserId());

        ArgumentCaptor<String> getLoginUserArgumentCaptor  = ArgumentCaptor.forClass(String.class);
        verify(mockUserService, times(1)).getLoginUser(getLoginUserArgumentCaptor.capture());
        String getLoginUserArgVal = getLoginUserArgumentCaptor.getValue();
        assertThat(getLoginUserArgVal).isEqualTo(mUser.getUserName());

    }

    @Test
    @DisplayName("該当ユーザが存在しない場合、エラーメッセージを表示")
    void testLoadUserByUsername1() {
        doReturn(null).when(mockUserService).getLoginUser(any());

        UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class, () ->
                userDetailsServiceImpl.loadUserByUsername(null));
        assertThat(e.getMessage()).isEqualTo("user not found");

        ArgumentCaptor<String> getLoginUserArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockUserService, times(1)).getLoginUser(getLoginUserArgumentCaptor.capture());
        assertThat(getLoginUserArgumentCaptor.getValue()).isEqualTo(null);

    }
}