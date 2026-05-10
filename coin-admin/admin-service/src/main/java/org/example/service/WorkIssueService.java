package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.domain.WorkIssue;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author cx540
* @description 针对表【work_issue(客服工单)】的数据库操作Service
* @createDate 2026-05-02 15:37:32
*/
public interface WorkIssueService extends IService<WorkIssue> {

    Page<WorkIssue> findByPage(Page<WorkIssue> page, int status, String startTime, String endTime);
}
