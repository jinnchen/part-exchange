package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.domain.Notice;
import org.example.service.NoticeService;
import org.example.mapper.NoticeMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
* @author cx540
* @description 针对表【notice(内容管理)】的数据库操作Service实现
* @createDate 2026-05-02 15:37:32
*/
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
    implements NoticeService{

    @Override
    public Page<Notice> findByPage(Page<Notice> page, String title, String startTime, String endTime, String status) {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getStatus,status);
        queryWrapper.like(!StringUtils.hasText(title), Notice::getTitle, title);
        queryWrapper.between(!StringUtils.hasText(startTime) && !StringUtils.hasText(startTime),
                Notice::getCreated, startTime, endTime + " 23:59:59");
        return page(page, queryWrapper);
    }

    @Override
    public boolean removeIds(List<String> list) {
        return false;
    }

}




