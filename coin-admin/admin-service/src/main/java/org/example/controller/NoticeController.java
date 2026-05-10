package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import org.example.domain.Notice;
import org.example.domain.SysUser;
import org.example.model.R;
import org.example.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/notics")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @GetMapping
    @Operation(description = "分页查询公告")
    @PreAuthorize("hasAuthority(notice_query)")
    public R<Page<Notice>> findByPage(int current,
                                      int size,
                                      String title,
                                      String startTime,
                                      String endTime,
                                      String status) {
        Page<Notice> page = new Page<>(current, size);
        page.addOrder(OrderItem.desc("last_update_time"));
        Page<Notice> notices = noticeService.findByPage(page, title, startTime, endTime, status);
        return R.ok(notices);
    }

    @PostMapping
    @Operation(description = "新增公告")
    @PreAuthorize("hasAuthority('notice_create')")
    public R<String> add(@RequestBody @Validated Notice notice) {
        notice.setStatus(1);
        boolean flag = noticeService.save(notice);
        if (flag) {
            return R.ok("新增成功");
        }
        return R.fail("新增失败");
    }

    @PostMapping("/update")
    @Operation(description = "修改公告")
    @PreAuthorize("hasAuthority('notice_update')")
    public R<String> update(@RequestBody Notice notice) {
        boolean flag = noticeService.updateById(notice);
        if (flag) {
            return R.ok("修改成功");
        }
        return R.fail("修改失败");
    }

    @PostMapping("/updateStatus")
    @Operation(description = "修改公告状态")
    @PreAuthorize("hasAuthority('notice_update')")
    public R<String> updateStatus(@RequestBody Long id, @RequestBody Integer status) {
        Notice notice = new Notice();
        notice.setId(id);
        notice.setStatus(status);
        boolean flag = noticeService.updateById(notice);
        if (flag) {
            return R.ok("修改成功");
        }
        return R.fail("修改失败");
    }

    @DeleteMapping
    @Operation(description = "删除公告")
    @PreAuthorize("hasAuthority('notice_delete')")
    public R<String> delete(@RequestBody String[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("要删除的ID不能为null");
        }
        boolean flag = noticeService.removeIds(Arrays.asList(ids));
        if (flag) {
            return R.ok("删除成功");
        }
        return R.fail("删除失败");
    }
}
