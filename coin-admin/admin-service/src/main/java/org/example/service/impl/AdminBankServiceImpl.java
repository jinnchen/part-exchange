package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.domain.AdminBank;
import org.example.service.AdminBankService;
import org.example.mapper.AdminBankMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author cx540
* @description 针对表【admin_bank】的数据库操作Service实现
* @createDate 2026-05-09 07:38:53
*/
@Service
public class AdminBankServiceImpl extends ServiceImpl<AdminBankMapper, AdminBank>
    implements AdminBankService{

    @Override
    public Page<AdminBank> findByPage(Page<AdminBank> page, String bankCard) {
        LambdaQueryWrapper<AdminBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(!StringUtils.hasText(bankCard), AdminBank::getBankCard, bankCard);
        return page(page, wrapper);
    }
}




