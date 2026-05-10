package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.domain.SysPrivilege;
import org.example.domain.SysUser;
import org.example.model.R;
import org.example.service.SysPrivilegeService;
import org.example.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/privileges")
@Tag(name = "权限管理", description = "权限相关接口")
public class SysPrivilegeController {

    @Autowired
    private SysPrivilegeService sysPrivilegeService;

    @Autowired
    private SysUserService sysUserService;


    @GetMapping
    @Operation(description = "分页查询权限列表")
    @PreAuthorize("hasAuthority('sys_privilege_query')")
    public R<Page<SysPrivilege>> findByPage(int current, int size){
        Page page = new Page(current, size);
        page.addOrder(OrderItem.desc("last_update_time"));

        Page<SysPrivilege> sysPrivilegePage = sysPrivilegeService.page(page);
        return R.ok(sysPrivilegePage);
    }

    @PostMapping
    @Operation(description = "新增一个权限")
    @PreAuthorize("hasAuthority('sys_privilege_create')")
    public R add(@RequestBody SysPrivilege sysPrivilege) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        String username = jwt.getSubject();
        
        SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        sysPrivilege.setCreateBy(sysUser.getId());
        
        boolean saved = sysPrivilegeService.save(sysPrivilege);
        return saved ? R.ok("新增成功") : R.fail("新增失败");
    }

    @PutMapping
    @Operation(description = "修改一个权限")
    @PreAuthorize("hasAuthority('sys_privilege_update')")
    public R update(@RequestBody @Validated SysPrivilege sysPrivilege) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        String username = jwt.getSubject();

        SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        sysPrivilege.setModifyBy(sysUser.getId());

        boolean saved = sysPrivilegeService.updateById(sysPrivilege);
        return saved ? R.ok("修改成功") : R.fail("修改失败");
    }
}
