package org.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.domain.WebConfig;
import org.example.domain.WorkIssue;
import org.example.model.R;
import org.example.service.WebConfigService;
import org.example.service.WorkIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@Tag(name = "客服工单控制器")
@RequestMapping("/workIssues")
public class WorkIssueController {

    @Autowired
    private WorkIssueService workIssueService;


    @GetMapping
    @Operation(description = "分页查询客服工单")
    @PreAuthorize("hasAuthority('work_issue_query')")
    public R<Page<WorkIssue>> findByPage(int current,
                                         int size,
                                         int status,
                                         String startTime,
                                         String endTime) {
        Page<WorkIssue> page = new Page<>(current, size);
        Page<WorkIssue> result = workIssueService.findByPage(page, status, startTime, endTime);
        return R.ok(result);
    }

    @PostMapping
    @Operation(description = "新增 workIssue")
    @PreAuthorize("hasAuthority('work_issue_create')")
    public R<String> add(@RequestBody @Validated WorkIssue workIssue) {
        boolean flag = workIssueService.save(workIssue);
        if (flag) {
            return R.ok("新增成功");
        }
        return R.fail("新增失败");
    }

    @PostMapping("/re")
    @Operation(description = "回复工单")
    @PreAuthorize("hasAuthority('work_issue_update')")
    public R<String> update(Long id, String answer) {
        WorkIssue workIssue = new WorkIssue();
        workIssue.setId(id);
        workIssue.setAnswer(answer);
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        boolean flag = workIssueService.updateById(workIssue);
        if (flag) {
            return R.ok("回复成功");
        }
        return R.fail("回复失败");
    }

    @DeleteMapping
    @Operation(description = "删除 workIssue")
    @PreAuthorize("hasAuthority('work_issue_delete')")
    public R<String> delete(@RequestBody String[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("要删除的ID不能为null");
        }
        boolean flag = workIssueService.removeByIds(Arrays.asList(ids));
        if (flag) {
            return R.ok("删除成功");
        }
        return R.fail("删除失败");
    }
}
