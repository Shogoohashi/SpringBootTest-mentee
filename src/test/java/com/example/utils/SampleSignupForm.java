package com.example.utils;

import com.example.form.SignupForm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.utils.Constants.*;

public class SampleSignupForm {
    public static SignupForm createSignupForm() {
        SignupForm signupForm = new SignupForm();

        signupForm.setUserId(GENERAL_USER_ID);
        signupForm.setPassword(PASSWORD);
        signupForm.setUserName(GENERAL_USER_NAME_A);
        signupForm.setBirthday(createDate());
        signupForm.setAge(20);
        signupForm.setGender(1);

        return signupForm;
    }

    private static Date createDate() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return simpleDateFormat.parse("2000/01/01 00:00:00");
        } catch (ParseException e) {
            throw new Error("parseに失敗しました。");
        }
    }
}
