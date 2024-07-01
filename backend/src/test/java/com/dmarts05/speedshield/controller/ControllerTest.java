package com.dmarts05.speedshield.controller;

import com.dmarts05.speedshield.service.AuthService;
import com.dmarts05.speedshield.service.JwtService;
import com.dmarts05.speedshield.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest
public abstract class ControllerTest {
    @MockBean
    UserDetailsServiceImpl userDetailsService;
    @MockBean
    JwtService jwtService;
    @MockBean
    AuthService authService;

    @Autowired
    WebApplicationContext webApplicationContext;

    MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }
}
