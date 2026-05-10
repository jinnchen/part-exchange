package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.domain.SysUserLog;
import org.example.model.R;
import org.example.service.SysUserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "用户操作记录查询")
@RequestMapping("/sysUserLog")
public class SysUserLogController {

    @Autowired
    private SysUserLogService sysUserLogService;

    @GetMapping
    @Operation(description = "分页查询用户操作记录")
    public R<Page<SysUserLog>> findByPage(int current, int size) {
        Page<SysUserLog> page = new Page<>(current, size);
        page.addOrder(OrderItem.desc("created"));
        return R.ok(sysUserLogService.page(page));
    }
}
