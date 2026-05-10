package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.model.LoginResult;
import org.example.service.SysLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "登录控制器")
public class SysLoginController {

    @Autowired
    private SysLoginService sysLoginService;

    @PostMapping("/login")
    @Operation(description = "后台登录")
    @Parameters({
            @Parameter(name = "username", description = "用户名"),
            @Parameter(name = "password", description = "用户密码")
    })
    public LoginResult login(
            @RequestParam(required = true) String username,
            @RequestParam(required = true) String password
    ) {
        System.out.println("SysLoginController login");
        return sysLoginService.login(username, password);
    }
}
