package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.domain.AdminBank;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author cx540
* @description 针对表【admin_bank】的数据库操作Service
* @createDate 2026-05-09 07:38:53
*/
public interface AdminBankService extends IService<AdminBank> {

    Page<AdminBank> findByPage(Page<AdminBank> page, String bankCard);
}
