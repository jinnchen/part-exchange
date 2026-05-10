package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.domain.SysMenu;
import org.example.domain.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author cx540
* @description 针对表【sys_user】的数据库操作Service
* @createDate 2026-05-02 15:37:32
*/
public interface SysUserService extends IService<SysUser> {
    List<SysUser> findByPage(Page page, String mobile, String fullname);

    boolean addUser(SysUser sysUser);

    boolean removeIds(List<String> list);
}
