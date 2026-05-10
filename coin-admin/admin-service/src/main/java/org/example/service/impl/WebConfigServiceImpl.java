package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.domain.WebConfig;
import org.example.service.WebConfigService;
import org.example.mapper.WebConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
* @author cx540
* @description 针对表【web_config(网站配置)】的数据库操作Service实现
* @createDate 2026-05-02 15:37:32
*/
@Service
public class WebConfigServiceImpl extends ServiceImpl<WebConfigMapper, WebConfig>
    implements WebConfigService{

    @Override
    public Page<WebConfig> findByPage(Page<WebConfig> page, String name, String type) {
        LambdaQueryWrapper<WebConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(!StringUtils.hasText(name), WebConfig::getName, name);
        wrapper.eq(!StringUtils.hasText(type), WebConfig::getType, type);
        return page(page, wrapper);
    }

}




