package org.example.service;

import org.example.domain.SysPrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author cx540
* @description 针对表【sys_privilege】的数据库操作Service
* @createDate 2026-05-02 15:37:32
*/
public interface SysPrivilegeService extends IService<SysPrivilege> {

    List<SysPrivilege> getAllPrivilege(Long menuId, Long roleId);
}
