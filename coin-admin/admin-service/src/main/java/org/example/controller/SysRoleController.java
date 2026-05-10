package org.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.domain.SysRole;
import org.example.model.R;
import org.example.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.Arrays;

@RestController
@RequestMapping("/roles")
@Tag(name = "角色管理")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping
    @Operation(description = "条件分页查询")
    @PreAuthorize("hasAuthority('sys_role_query')")
    public R<Page<SysRole>> findByPage(int current, int size,
                                       @Parameter(description = "角色关键字", required = false) @RequestParam(required = false) String name) {
        Page page = new Page(current, size);
        Page<SysRole> sysRolePage = sysRoleService.findByPage(page, name);
        return R.ok(sysRolePage);
    }

    @PostMapping
    @Operation(description = "新增角色")
    @PreAuthorize("hasAuthority('sys_role_create')")
    public R<String> add(@RequestBody @Validated SysRole sysRole) {
        boolean flag = sysRoleService.save(sysRole);
        if (flag) {
            return R.ok("新增成功");
        }
        return R.fail("新增失败");
    }

    @DeleteMapping
    @Operation(description = "删除角色")
    @PreAuthorize("hasAuthority('sys_role_delete')")
    public R<String> update(@RequestBody String[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("要删除的ID不能为null");
        }
        boolean flag = sysRoleService.removeBatchByIds(Arrays.asList(ids));
        if (flag) {
            return R.ok("删除成功");
        }
        return R.fail("删除失败");
    }
}
