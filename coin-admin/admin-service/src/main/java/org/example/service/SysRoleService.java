package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.domain.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author cx540
* @description 针对表【sys_role】的数据库操作Service
* @createDate 2026-05-02 15:37:32
*/
public interface SysRoleService extends IService<SysRole> {

    Page<SysRole> findByPage(Page<SysRole> page, String name);
}
