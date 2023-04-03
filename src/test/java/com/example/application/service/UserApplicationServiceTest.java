package com.example.application.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    @DisplayName("getGenderMapのリクエストが返ってきた場合、男性のみ取得する")
    void getGenderMap1() {
        doReturn("男性").when(messageSource).getMessage(anyString(), any(), any(Locale.class));

        Map<String, Integer> actual = userApplicationService.getGenderMap(Locale.JAPAN);

        verify(messageSource, times(1)).getMessage(eq("male"), any(), any(Locale.class));

        Map<String, Integer> expected = new HashMap<>();
        expected.put("男性", 2);

        assertThat(actual).isEqualTo(expected);

    }

    @Test
    @DisplayName("getGenderMapのリクエストが返ってきた場合、女性のみ取得する")
    void getGenderMap2() {
        doReturn("女性").when(messageSource).getMessage(anyString(), any(), any(Locale.class));

        Map<String, Integer> actual = userApplicationService.getGenderMap(Locale.JAPAN);

        verify(messageSource, times(1)).getMessage(eq("female"), any(), any(Locale.class));

        Map<String, Integer> expected = new HashMap<>();
        expected.put("女性", 2);

        assertThat(actual).isEqualTo(expected);

    }
}