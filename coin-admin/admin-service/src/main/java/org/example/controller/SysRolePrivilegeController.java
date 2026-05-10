package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.domain.SysMenu;
import org.example.domain.SysPrivilege;
import org.example.domain.SysRole;
import org.example.domain.SysUser;
import org.example.model.R;
import org.example.model.RolePrivilegesParam;
import org.example.service.SysRolePrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "角色权限管理")
public class SysRolePrivilegeController {

    @Autowired
    private SysRolePrivilegeService sysRolePrivilegeService;

    @GetMapping
    @Operation(description = "条件分页查询")
    @PreAuthorize("hasAuthority('sys_role_query')")
    public R<List<SysMenu>> findByPage(int current, int size, Long roleId) {
        Page page = new Page(current, size);
        List<SysMenu> sysRolePage = sysRolePrivilegeService.findByPage(page, roleId);
        return R.ok(sysRolePage);
    }

    @PostMapping("/grant_privileges")
    @Operation(description = "授予角色权限")
    public R grantPrivileges(@RequestBody RolePrivilegesParam rolePrivilegesParam) {
        boolean flag = sysRolePrivilegeService.grantPrivileges(rolePrivilegesParam);
        return flag ? R.ok("授予成功") : R.fail("授予失败");
    }
}
