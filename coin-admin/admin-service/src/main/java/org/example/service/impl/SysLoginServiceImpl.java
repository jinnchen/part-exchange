package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.*;
import org.example.feign.OAuthFeignClient;
import org.example.feign.jwtToken;
import org.example.model.LoginResult;
import org.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysLoginServiceImpl implements SysLoginService {

    @Autowired
    private OAuthFeignClient oAuthFeignClient;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleMenuService  sysRoleMenuService;

    @Autowired
    private SysRolePrivilegeUserService sysRolePrivilegeUserService;

    @Autowired
    private SysPrivilegeService sysPrivilegeService;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:http://localhost:9999}")
    private String issuerUri;

    @Override
    public LoginResult login(String username, String password) {
        log.info("用户{}登录", username);
        ResponseEntity<jwtToken> tokenResponseEntity = oAuthFeignClient.getToken("client_credentials", username, password, "all");
        if (tokenResponseEntity.getStatusCode() != HttpStatus.OK) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        String token = null;
        if (tokenResponseEntity.getBody() != null) {
            token = tokenResponseEntity.getBody().getAccessToken();
        }

        // 解析 JWT 获取用户信息
        Jwt jwt = parseJwt(token);
        String jwtUsername = null;
        if (jwt != null) {
            jwtUsername = jwt.getClaimAsString("username");
        }
        log.info("JWT 解析成功，用户名: {}", jwtUsername);
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>();
        queryWrapper.eq(SysUser::getUsername, jwtUsername);
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        long userId = sysUser.getId();
        LambdaQueryWrapper<SysUserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper.eq(SysUserRole::getUserId, userId);
        SysUserRole sysUserRole = sysUserRoleService.getOne(userRoleLambdaQueryWrapper);
        Long roleId = sysUserRole.getRoleId();
        LambdaQueryWrapper<SysRoleMenu> sysRoleMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysRoleMenuLambdaQueryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuService.list(sysRoleMenuLambdaQueryWrapper);
        List<Long> menusIds = new ArrayList<>();
        sysRoleMenus.forEach(sysRoleMenu -> {
            menusIds.add(sysRoleMenu.getMenuId());
        });
        List<SysMenu> menus = sysMenuService.listByIds(menusIds);

        System.out.println("===========start==========");
        System.out.println(jwt.getClaimAsString("authorities"));
        System.out.println("===========end==========");
        Object claim = jwt.getClaim("authorities");
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (claim instanceof String str) {
            if (str.startsWith("[")) {
                str = str.substring(1, str.length() - 1);
                for (String auth : str.split(",")) {
                    auth = auth.trim();
                    if (!auth.isEmpty()) {
                        authorities.add(new SimpleGrantedAuthority(auth));
                    }
                }
            }
        }
        return new LoginResult("Bearer" + token, menus, authorities);
    }

    private Jwt parseJwt(String token) {
        try {
            JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            log.error("JWT 解析失败: {}", e.getMessage());
            return null;
        }
    }
}
