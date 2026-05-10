package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.domain.SysPrivilege;
import org.example.service.SysPrivilegeService;
import org.example.mapper.SysPrivilegeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
* @author cx540
* @description 针对表【sys_privilege】的数据库操作Service实现
* @createDate 2026-05-02 15:37:32
*/
@Service
public class SysPrivilegeServiceImpl extends ServiceImpl<SysPrivilegeMapper, SysPrivilege>
    implements SysPrivilegeService{

    @Autowired
    private SysPrivilegeMapper sysPrivilegeMapper;

    @Override
    public List<SysPrivilege> getAllPrivilege(Long menuId, Long roleId) {
        List<SysPrivilege> sysPrivileges = list(new LambdaQueryWrapper<SysPrivilege>()
                .eq(SysPrivilege::getMenuId, menuId));
        if (CollectionUtils.isEmpty(sysPrivileges)) {
            return Collections.emptyList();
        }
        for (SysPrivilege sysPrivilege : sysPrivileges) {
            Set<Long> currentRoleSysPrivilegeIds = sysPrivilegeMapper.getPrivilegesByRoleId(roleId);
            if (currentRoleSysPrivilegeIds.contains(sysPrivilege.getId())) {
                sysPrivilege.setOwn(1);
            }
        }
        return sysPrivileges;
    }
}




