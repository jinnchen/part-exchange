package org.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.domain.Notice;
import org.example.domain.WebConfig;
import org.example.model.R;
import org.example.service.WebConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@Tag(name = "webConfig控制器")
@RequestMapping("/webConfigs")
public class WebConfigController {

    @Autowired
    private WebConfigService webConfigService;

    @GetMapping
    @Operation(description = "分页查询webConfig")
    @PreAuthorize("hasAuthority('web_config_query')")
    public R<Page<WebConfig>> findByPage(int current, int size, String name, String type) {
        Page<WebConfig> page = new Page<>(current, size);
        Page<WebConfig> result = webConfigService.findByPage(page, name, type);
        return R.ok(result);
    }

    @PostMapping
    @Operation(description = "新增webConfig")
    @PreAuthorize("hasAuthority('web_config_create')")
    public R<String> add(@RequestBody @Validated WebConfig webConfig) {
        boolean flag = webConfigService.save(webConfig);
        if (flag) {
            return R.ok("新增成功");
        }
        return R.fail("新增失败");
    }

    @PostMapping("/update")
    @Operation(description = "修改webConfig")
    @PreAuthorize("hasAuthority('web_config_update')")
    public R<String> update(@RequestBody WebConfig webConfig) {
        boolean flag = webConfigService.updateById(webConfig);
        if (flag) {
            return R.ok("修改成功");
        }
        return R.fail("修改失败");
    }

    @DeleteMapping
    @Operation(description = "删除webConfig")
    @PreAuthorize("hasAuthority('web_config_delete')")
    public R<String> delete(@RequestBody String[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("要删除的ID不能为null");
        }
        boolean flag = webConfigService.removeByIds(Arrays.asList(ids));
        if (flag) {
            return R.ok("删除成功");
        }
        return R.fail("删除失败");
    }
}
