package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.domain.SysMenu;
import org.example.domain.SysRole;
import org.example.domain.SysUser;
import org.example.model.R;
import org.example.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "员工管理")
@RequestMapping("/users")
@RestController
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping
    @Operation(description = "条件分页查询")
    @PreAuthorize("hasAuthority('sys_user_query')")
    public R<List<SysUser>> findByPage(int current, int size, String mobile, String fullname) {
        Page page = new Page(current, size);
        page.addOrder(OrderItem.desc("last_update_time"));
        List<SysUser> sysUsers = sysUserService.findByPage(page, mobile, fullname);
        return R.ok(sysUsers);
    }
    @PostMapping
    @Operation(description = "新增用户")
    @PreAuthorize("hasAuthority('sys_role_create')")
    public R<String> add(@RequestBody @Validated SysUser sysUser) {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        boolean flag = sysUserService.addUser(sysUser);
        if (flag) {
            return R.ok("新增成功");
        }
        return R.fail("新增失败");
    }

    @DeleteMapping
    @Operation(description = "删除用户")
    @PreAuthorize("hasAuthority('sys_user_delete')")
    public R<String> delete(@RequestBody String[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("要删除的ID不能为null");
        }
        boolean flag = sysUserService.removeIds(Arrays.asList(ids));
        if (flag) {
            return R.ok("删除成功");
        }
        return R.fail("删除失败");
    }
}
