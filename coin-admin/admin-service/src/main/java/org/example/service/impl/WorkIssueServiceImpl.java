package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.domain.WorkIssue;
import org.example.service.WorkIssueService;
import org.example.mapper.WorkIssueMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author cx540
* @description 针对表【work_issue(客服工单)】的数据库操作Service实现
* @createDate 2026-05-02 15:37:32
*/
@Service
public class WorkIssueServiceImpl extends ServiceImpl<WorkIssueMapper, WorkIssue>
    implements WorkIssueService{

    @Override
    public Page<WorkIssue> findByPage(Page<WorkIssue> page, int status, String startTime, String endTime) {
        LambdaQueryWrapper<WorkIssue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkIssue::getStatus,status);
        wrapper.between(!StringUtils.hasText(startTime) &&!StringUtils.hasText(endTime),
                WorkIssue::getCreated,
                startTime,
                endTime + " 23:59:59");
        return page(page, wrapper);
    }
}




