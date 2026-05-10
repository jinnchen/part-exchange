package org.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.domain.AdminBank;
import org.example.domain.Config;
import org.example.model.R;
import org.example.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/configs")
@Tag(name = "后台参数配置")
public class ConfigController {
    @Autowired
    private ConfigService configService;


    @GetMapping
    @Operation(description = "分页查询config")
    @PreAuthorize("hasAuthority('config_query')")
    public R<Page<Config>> findByPage(int current,
                                      int size,
                                      String type,
                                      String code,
                                      String name) {
        Page<Config> page = new Page<>(current, size);
        Page<Config> result = configService.findByPage(page, type, code, name);
        return R.ok(result);
    }

    @PostMapping
    @Operation(description = "新增config")
    @PreAuthorize("hasAuthority('config_create')")
    public R<String> add(@RequestBody @Validated Config config) {
        boolean flag = configService.save(config);
        if (flag) {
            return R.ok("新增成功");
        }
        return R.fail("新增失败");
    }

    @PutMapping
    @Operation(description = "修改config")
    @PreAuthorize("hasAuthority('config_update')")
    public R<String> update(@RequestBody @Validated Config config) {
        boolean flag = configService.updateById(config);
        if (flag) {
            return R.ok("修改成功");
        }
        return R.fail("修改失败");
    }

    @DeleteMapping
    @Operation(description = "删除config")
    @PreAuthorize("hasAuthority('config_delete')")
    public R<String> delete(@RequestBody String[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("要删除的ID不能为null");
        }
        boolean flag = configService.removeByIds(Arrays.asList(ids));
        if (flag) {
            return R.ok("删除成功");
        }
        return R.fail("删除失败");
    }
}
