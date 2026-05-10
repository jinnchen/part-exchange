package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.example.domain.SysMenu;
import org.example.domain.SysRole;
import org.example.domain.SysUser;
import org.example.domain.SysUserRole;
import org.example.model.R;
import org.example.service.SysUserRoleService;
import org.example.service.SysUserService;
import org.example.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author cx540
* @description 针对表【sys_user】的数据库操作Service实现
* @createDate 2026-05-02 15:37:32
*/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public List<SysUser> findByPage(Page page, String mobile, String fullname) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(mobile), SysUser::getMobile, mobile)
                .like(!StringUtils.isEmpty(fullname), SysUser::getFullname, fullname);
        List<SysUser> sysUsers = (List<SysUser>) page(page, queryWrapper);
        if (!CollectionUtils.isEmpty(sysUsers)) {
            for (SysUser sysUser : sysUsers) {
                LambdaQueryWrapper<SysUserRole> userRoleQueryWrapper = new LambdaQueryWrapper<>();
                userRoleQueryWrapper.eq(SysUserRole::getUserId, sysUser.getId());
                List<SysUserRole> sysUserRoles = sysUserRoleService.list(userRoleQueryWrapper);
                if (!CollectionUtils.isEmpty(sysUserRoles)) {
                    sysUser.setRole_strings(sysUserRoles.stream()
                            .map(sysUserRole -> sysUserRole.getRoleId().toString())
                            .collect(Collectors.joining(",")));
                }
            }
        }
        return sysUsers;
    }

    @Override
    @Transactional
    public boolean addUser(SysUser sysUser) {
        String password = sysUser.getPassword();
        String encoded = new BCryptPasswordEncoder().encode(password);
        sysUser.setPassword(encoded);
        boolean saved = save(sysUser);
        if (saved) {
            String[] roleIds = sysUser.getRole_strings().split(",");
            List<SysUserRole> sysUserRoleList = new ArrayList<>();
            for (String roleId : roleIds) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(Long.parseLong(roleId));
                sysUserRole.setUserId(sysUser.getId());
                sysUserRoleList.add(sysUserRole);
            }
            sysUserRoleService.saveBatch(sysUserRoleList);
        }
        return saved;
    }

    @Override
    public boolean removeIds(List<String> list) {
        removeByIds(list);
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysUserRole::getId, list);
        sysUserRoleService.remove(queryWrapper);
        return false;
    }

}




