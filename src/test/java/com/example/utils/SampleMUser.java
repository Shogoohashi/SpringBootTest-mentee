package com.example.utils;

import com.example.domain.user.model.Department;
import com.example.domain.user.model.MUser;
import com.example.domain.user.model.Salary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static com.example.utils.Constants.*;

public class SampleMUser {
    public static MUser createAdminUser() {
        MUser mUser = new MUser();

        mUser.setUserId(ADMIN_USER_ID);
        mUser.setPassword(ENCODED_PASSWORD);
        mUser.setUserName(ADMIN_USER_NAME);
        mUser.setBirthday(createDate("2000/01/01 00:00:00"));
        mUser.setAge(21);
        mUser.setGender(1);
        mUser.setDepartmentId(1);
        mUser.setRole("ROLE_ADMIN");
        mUser.setDepartment(createDepartmentA());
        mUser.setSalaryList(Arrays.asList(createSalaryA(), createSalaryB(), createSalaryC()));

        return mUser;
    }

    public static MUser createGeneralUserA() {
        MUser mUser = new MUser();

        mUser.setUserId(GENERAL_USER_ID);
        mUser.setPassword(ENCODED_PASSWORD);
        mUser.setUserName(GENERAL_USER_NAME_A);
        mUser.setBirthday(createDate("2000/01/01 00:00:00"));
        mUser.setAge(21);
        mUser.setGender(2);
        mUser.setDepartmentId(2);
        mUser.setRole("ROLE_GENERAL");
        mUser.setDepartment(createDepartmentB());
        mUser.setSalaryList(Arrays.asList(createSalaryA(), createSalaryB(), createSalaryC()));

        return mUser;
    }

    public static MUser createGeneralUserB() {
        MUser mUser = new MUser();

        mUser.setUserId(GENERAL_USER_ID);
        mUser.setPassword(ENCODED_PASSWORD);
        mUser.setUserName(GENERAL_USER_NAME_B);
        mUser.setBirthday(createDate("2022/12/01 12:00:00"));
        mUser.setAge(20);
        mUser.setGender(1);
        mUser.setDepartmentId(1);
        mUser.setRole("ROLE_GENERAL");
        mUser.setDepartment(createDepartmentB());
        mUser.setSalaryList(Arrays.asList(createSalaryA(), createSalaryB(), createSalaryC()));

        return mUser;
    }

    public static Department createDepartmentA() {
        Department department = new Department();

        department.setDepartmentId(1);
        department.setDepartmentName("システム管理部");

        return department;
    }

    public static Department createDepartmentB() {
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

    private static Date createDate(String dateTime) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return simpleDateFormat.parse(dateTime);
        } catch (ParseException e) {
            throw new Error("parseに失敗しました。");
        }
    }
}
