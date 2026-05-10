package org.example.mapper;

import org.example.domain.SysPrivilege;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Set;

/**
* @author cx540
* @description 针对表【sys_privilege】的数据库操作Mapper
* @createDate 2026-05-02 15:37:32
* @Entity org.example.domain.SysPrivilege
*/
public interface SysPrivilegeMapper extends BaseMapper<SysPrivilege> {

    Set<Long> getPrivilegesByRoleId(Long roleId);
}




