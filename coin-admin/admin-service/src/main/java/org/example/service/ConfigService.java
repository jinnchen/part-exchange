package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.domain.Config;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author cx540
* @description 针对表【config】的数据库操作Service
* @createDate 2026-05-02 15:37:32
*/
public interface ConfigService extends IService<Config> {

    Page<Config> findByPage(Page<Config> page, String type, String code, String name);
}
