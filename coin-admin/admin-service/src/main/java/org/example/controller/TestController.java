package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.domain.SysUser;
import org.example.model.R;
import org.example.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "后台管理测试接口")
public class TestController {

    @Autowired
    private SysUserService sysUserService;

    @Operation(description = "查询用户信息")
    @Parameters({
            @Parameter(name = "id", description = "用户ID")
    })
    @GetMapping("/user/info/{id}")
    public R<SysUser> getSysUser(@PathVariable("id") Long id) {
        SysUser sysUser = sysUserService.getById(id);
        return R.ok(sysUser);
    }
}
