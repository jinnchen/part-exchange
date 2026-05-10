package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.domain.SysRole;
import org.example.service.SysRoleService;
import org.example.mapper.SysRoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author cx540
 * @description 针对表【sys_role】的数据库操作Service实现
 * @createDate 2026-05-02 15:37:32
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

    @Override
    public Page<SysRole> findByPage(Page<SysRole> page, String name) {
        return page(page, new LambdaQueryWrapper<SysRole>().like(
                !StringUtils.isEmpty(name),
                SysRole::getName,
                name
        ));
    }
}




