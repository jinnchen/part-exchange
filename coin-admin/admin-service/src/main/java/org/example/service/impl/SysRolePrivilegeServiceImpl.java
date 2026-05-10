package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.domain.SysMenu;
import org.example.domain.SysPrivilege;
import org.example.domain.SysRolePrivilege;
import org.example.mapper.SysMenuMapper;
import org.example.mapper.SysPrivilegeMapper;
import org.example.model.RolePrivilegesParam;
import org.example.service.SysPrivilegeService;
import org.example.service.SysRolePrivilegeService;
import org.example.mapper.SysRolePrivilegeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cx540
 * @description 针对表【sys_role_privilege】的数据库操作Service实现
 * @createDate 2026-05-02 15:37:32
 */
@Service
public class SysRolePrivilegeServiceImpl extends ServiceImpl<SysRolePrivilegeMapper, SysRolePrivilege>
        implements SysRolePrivilegeService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysPrivilegeService sysPrivilegeService;

    @Override
    public List<SysMenu> findByPage(Page page, Long roleId) {
        List<SysMenu> list = sysMenuMapper.selectList(null);
        if (list.isEmpty()) {
            return list;
        }
        List<SysMenu> rootMenus = list
                .stream()
                .filter(sysMenu -> sysMenu.getParentId() == null)
                .toList();
        if (rootMenus.isEmpty()) {
            return rootMenus;
        }
        List<SysMenu> subMenus = new ArrayList<>();
        for (SysMenu rootMenu : rootMenus) {
            subMenus.addAll(getSubMenus(rootMenu.getId(), roleId, list));
        }
        return List.of();
    }

    private List<SysMenu> getSubMenus(Long parentId, Long roleId, List<SysMenu> sources) {
        ArrayList<SysMenu> subMenus = new ArrayList<>();
        for (SysMenu source : sources) {
            if (source.getParentId().equals(parentId)) {
                subMenus.add(source);
                source.setChildren(getSubMenus(source.getId(), roleId, sources));
                List<SysPrivilege> privileges = sysPrivilegeService.getAllPrivilege(source.getId(), roleId);
                source.setPrivileges(privileges);
            }
        }
        return subMenus;
    }

    @Override
    public boolean grantPrivileges(RolePrivilegesParam rolePrivilegesParam) {
        Long roleId = rolePrivilegesParam.getRoleId();
        LambdaQueryWrapper<SysRolePrivilege> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRolePrivilege::getRoleId, roleId);
        remove(queryWrapper);
        List<Long> privileges = rolePrivilegesParam.getPrivilegeIds();
        if (CollectionUtils.isEmpty(privileges)) {
            List<SysRolePrivilege> sysRolePrivilegeList = new ArrayList<>();
            for (Long privilege : privileges) {
                SysRolePrivilege sysRolePrivilege = new SysRolePrivilege();
                sysRolePrivilege.setRoleId(roleId);
                sysRolePrivilege.setPrivilegeId(privilege);
                sysRolePrivilegeList.add(sysRolePrivilege);
            }
            return saveBatch(sysRolePrivilegeList);
        }
        return true;
    }
}




