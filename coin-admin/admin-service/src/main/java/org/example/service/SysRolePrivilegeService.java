package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.domain.SysMenu;
import org.example.domain.SysRole;
import org.example.domain.SysRolePrivilege;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.RolePrivilegesParam;

import java.util.List;

/**
* @author cx540
* @description 针对表【sys_role_privilege】的数据库操作Service
* @createDate 2026-05-02 15:37:32
*/
public interface SysRolePrivilegeService extends IService<SysRolePrivilege> {

    List<SysMenu> findByPage(Page page, Long roleId);

    boolean grantPrivileges(RolePrivilegesParam rolePrivilegesParam);
}
