package org.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.domain.AdminBank;
import org.example.domain.WorkIssue;
import org.example.model.R;
import org.example.service.AdminBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/adminBank")
@Tag(name = "")
public class AdminBankController {
    @Autowired
    private AdminBankService adminBankService;

    @GetMapping
    @Operation(description = "分页查询银行卡")
    @PreAuthorize("hasAuthority('admin_bank_query')")
    public R<Page<AdminBank>> findByPage(int current,
                                                int size,
                                                String bankCard) {
        Page<AdminBank> page = new Page<>(current, size);
        Page<AdminBank> result = adminBankService.findByPage(page, bankCard);
        return R.ok(result);
    }

    @PostMapping
    @Operation(description = "新增银行卡")
    @PreAuthorize("hasAuthority('admin_bank_create')")
    public R<String> add(@RequestBody @Validated AdminBank adminBank) {
        boolean flag = adminBankService.save(adminBank);
        if (flag) {
            return R.ok("新增成功");
        }
        return R.fail("新增失败");
    }

    @PutMapping
    @Operation(description = "修改银行卡")
    @PreAuthorize("hasAuthority('admin_bank_update')")
    public R<String> update(@RequestBody @Validated AdminBank adminBank) {
        boolean flag = adminBankService.updateById(adminBank);
        if (flag) {
            return R.ok("修改成功");
        }
        return R.fail("修改失败");
    }

    @PostMapping("/updateStatus")
    @Operation(description = "修改银行卡")
    @PreAuthorize("hasAuthority('admin_bank_update')")
    public R<String> updateStatus(Long bankId, Integer status) {
        AdminBank adminBank = new AdminBank();
        adminBank.setId(bankId);
        adminBank.setStatus(status);
        boolean flag = adminBankService.updateById(adminBank);
        if (flag) {
            return R.ok("修改成功");
        }
        return R.fail("修改失败");
    }

    @DeleteMapping
    @Operation(description = "删除银行卡")
    @PreAuthorize("hasAuthority('admin_bank_delete')")
    public R<String> delete(@RequestBody String[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("要删除的ID不能为null");
        }
        boolean flag = adminBankService.removeByIds(Arrays.asList(ids));
        if (flag) {
            return R.ok("删除成功");
        }
        return R.fail("删除失败");
    }
}
