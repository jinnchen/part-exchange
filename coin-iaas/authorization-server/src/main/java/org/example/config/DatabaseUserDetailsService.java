package org.example.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT `id`, `username`, `password`, `status` FROM `sys_user` WHERE `username` = ?";

        List<UserDetails> users = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String userName = rs.getString("username");
            String password = rs.getString("password");
            Integer status = rs.getInt("status");

            List<GrantedAuthority> authorities = loadAuthorities(id);

            return User.builder()
                    .username(userName)
                    .password(password)
                    .authorities(authorities)
                    .disabled(status == 0)
                    .build();
        }, username);

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return users.get(0);
    }

    private List<GrantedAuthority> loadAuthorities(Long userId) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        String roleSql = "SELECT r.`code` FROM sys_role r " +
                "LEFT JOIN sys_user_role ur ON r.id = ur.role_id " +
                "WHERE ur.user_id = ?";

        List<String> roles = jdbcTemplate.query(roleSql, (rs, rowNum) -> rs.getString("code"), userId);
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        String privSql = "SELECT p.`name` FROM sys_privilege p " +
                "LEFT JOIN sys_role_privilege rp ON p.id = rp.privilege_id " +
                "LEFT JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
                "WHERE ur.user_id = ?";

        List<String> privs = jdbcTemplate.query(privSql, (rs, rowNum) -> rs.getString("name"), userId);
        for (String priv : privs) {
            if (priv != null) {
                authorities.add(new SimpleGrantedAuthority(priv));
            }
        }

        return authorities;
    }
}
