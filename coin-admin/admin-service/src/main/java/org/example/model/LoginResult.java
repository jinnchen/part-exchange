package org.example.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.domain.SysMenu;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(name="登录的结果")
public class LoginResult {
    @Schema(name = "登录成功的token")
    private String token;
    @Schema(name = "菜单数据")
    private List<SysMenu> menuList;
    @Schema(name = "权限数据")
    private List<SimpleGrantedAuthority> authorities;
}
