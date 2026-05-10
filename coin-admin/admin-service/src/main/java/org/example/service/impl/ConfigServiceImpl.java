package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.domain.Config;
import org.example.service.ConfigService;
import org.example.mapper.ConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author cx540
* @description 针对表【config】的数据库操作Service实现
* @createDate 2026-05-02 15:37:32
*/
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config>
    implements ConfigService{

    @Override
    public Page<Config> findByPage(Page<Config> page, String type, String code, String name) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(!StringUtils.hasText(type), Config::getType, type);
        wrapper.eq(!StringUtils.hasText(code), Config::getCode, code);
        wrapper.eq(!StringUtils.hasText(name), Config::getName, name);
        return page(page, wrapper);
    }
}




