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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    UserService mockUserService;

    @InjectMocks
    UserDetailsServiceImpl UserDetailsServiceImpl;

    @Test
    @DisplayName("該当ユーザが存在した場合、該当するユーザを取得する")
    void testLoadUserByUsername() {
        MUser testLoginUser = createGeneralUserA();
        doReturn(testLoginUser).when(mockUserService).getLoginUser(any());

        String mUser = "ユーザー１";
        MUser actual = mockUserService.getLoginUser(mUser);

        assertThat(actual.getUserName()).isEqualTo(testLoginUser.getUserName());

        ArgumentCaptor<String> loginUserArgCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockUserService, times(1)).getLoginUser(loginUserArgCaptor.capture());
        assertThat(loginUserArgCaptor.getValue()).isEqualTo(mUser);

    }

    @Test
    @DisplayName("該当ユーザが存在しない場合、エラーメッセージを表示")
    void testLoadUserByUsername1() {
        doReturn(null).when(mockUserService).getLoginUser(any());

        UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class, () ->
                UserDetailsServiceImpl.loadUserByUsername(null));
        assertThat(e.getMessage().equals("user not found"));

        ArgumentCaptor<String> UsernameNotFoundExceptionArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockUserService, times(1)).getLoginUser(UsernameNotFoundExceptionArgumentCaptor.capture());
        assertThat(UsernameNotFoundExceptionArgumentCaptor.getValue()).isEqualTo(null);

    }
}