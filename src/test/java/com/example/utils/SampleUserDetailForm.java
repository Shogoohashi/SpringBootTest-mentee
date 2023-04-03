package com.example.utils;

import com.example.domain.user.model.Department;
import com.example.domain.user.model.Salary;
import com.example.form.UserDetailForm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static com.example.utils.Constants.*;

public class SampleUserDetailForm {
    public static UserDetailForm createUserDetailForm() {
        UserDetailForm userDetailForm = new UserDetailForm();
        userDetailForm.setUserId(GENERAL_USER_ID);
        userDetailForm.setPassword(PASSWORD);
        userDetailForm.setUserName(GENERAL_USER_NAME_A);
        userDetailForm.setBirthday(createDate());
        userDetailForm.setAge(21);
        userDetailForm.setGender(2);
        userDetailForm.setDepartment(createDepartment());
        userDetailForm.setSalaryList(Arrays.asList(createSalaryA(), createSalaryB(), createSalaryC()));

        return userDetailForm;
    }

    public static Department createDepartment() {
        Department department = new Department();

        department.setDepartmentId(2);
        department.setDepartmentName("営業部");

        return department;
    }

    private static Salary createSalaryA() {
        Salary salary = new Salary();

        salary.setUserId(GENERAL_USER_ID);
        salary.setYearMonth("2020/11");
        salary.setSalary(280000);

        return salary;
    }

    private static Salary createSalaryB() {
        Salary salary = new Salary();

        salary.setUserId(GENERAL_USER_ID);
        salary.setYearMonth("2020/12");
        salary.setSalary(290000);

        return salary;
    }

    private static Salary createSalaryC() {
        Salary salary = new Salary();

        salary.setUserId(GENERAL_USER_ID);
        salary.setYearMonth("2021/01");
        salary.setSalary(300000);

        return salary;
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
