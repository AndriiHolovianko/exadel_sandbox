package com.exadel.sandbox.impl;

import com.exadel.sandbox.dto.response.user.UserResponse;
import com.exadel.sandbox.mappers.user.UserMapper;
import com.exadel.sandbox.model.user.Role;
import com.exadel.sandbox.service.impl.UserServiceImpl;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;


@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserMapper userMapper;

    @Test
    void findAll() {
    }

    @Test
    void findByName() {
        UserResponse user = userService.findByName("boris@gmail.com");
        Assert.assertTrue(CoreMatchers.is(user.getRole()).matches(Collections.singleton(Role.MODERATOR)));
        Assert.assertEquals(user.getFirstName(),"Boris");
    }
}