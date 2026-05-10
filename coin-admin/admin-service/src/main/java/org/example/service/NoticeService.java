package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.domain.Notice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author cx540
* @description 针对表【notice(内容管理)】的数据库操作Service
* @createDate 2026-05-02 15:37:32
*/
public interface NoticeService extends IService<Notice> {

    Page<Notice> findByPage(Page<Notice> page, String title, String startTime, String endTime, String status);

    boolean removeIds(List<String> list);
}
