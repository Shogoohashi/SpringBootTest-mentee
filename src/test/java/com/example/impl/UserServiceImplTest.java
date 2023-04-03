package com.example.impl;

import com.example.domain.user.model.MUser;
import com.example.domain.user.service.impl.UserServiceImpl;
import com.example.repository.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static com.example.utils.SampleMUser.createGeneralUserA;
import static com.example.utils.SampleMUser.createGeneralUserB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserMapper mockUserMapper;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    @DisplayName("正常系: findManyを1回呼ぶこと, 該当するユーザーの情報を返すこと")
    void getUsers() {
        List<MUser> findManyReturnedVal = Arrays.asList(createGeneralUserA(), createGeneralUserB());
        doReturn(findManyReturnedVal).when(mockUserMapper).findMany(any());

        MUser mUser = createGeneralUserA();
        List<MUser> actual = userService.getUsers(mUser);

        assertThat(actual.size()).isEqualTo(findManyReturnedVal.size());
        for (int i = 0; i < actual.size(); i++) {
            assertThat(actual.get(i)).usingRecursiveComparison().isEqualTo(findManyReturnedVal.get(i));
        }

        ArgumentCaptor<MUser> findManyArgCaptor1 = ArgumentCaptor.forClass(MUser.class);
        verify(mockUserMapper, times(1)).findMany(findManyArgCaptor1.capture());
        assertThat(findManyArgCaptor1.getValue()).usingRecursiveComparison().isEqualTo(mUser);
    }
}
