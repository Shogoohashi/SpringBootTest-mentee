package com.example.application.service;

import java.util.Locale;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    MessageSource messageSource;

    @InjectMocks
    UserApplicationService userApplicationService;

    @Test
    @DisplayName("getGenderMapのリクエストが返ってきた場合、選択された値の性別を返す")
    void testGetGenderMap() {
        String testFemale = "female";
        doReturn("男性").when(messageSource).getMessage(eq("male"), any(), any(Locale.class));
        doReturn("女性").when(messageSource).getMessage(eq("female"), any(), any(Locale.class));

        userApplicationService.getGenderMap(Locale.JAPAN);

        ArgumentCaptor<String> getGenderMapCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageSource, times(2)).getMessage(getGenderMapCaptor.capture(), any(), any(Locale.class));
        String getGenderMapArgVal = getGenderMapCaptor.getValue();
        assertThat(getGenderMapArgVal).isEqualTo(testFemale);

    }
}