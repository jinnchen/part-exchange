package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.domain.WebConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author cx540
* @description 针对表【web_config(网站配置)】的数据库操作Service
* @createDate 2026-05-02 15:37:32
*/
public interface WebConfigService extends IService<WebConfig> {

    Page<WebConfig> findByPage(Page<WebConfig> page, String name, String type);
}
