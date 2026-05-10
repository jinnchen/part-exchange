package org.example.model;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Tag(name = "接收角色和权限数据")
public class RolePrivilegesParam {
    @Parameter(name = "角色的ID")
    private Long roleId;

    @Parameter(name = "角色包含的权限")
    private List<Long> privilegeIds = Collections.emptyList();
}
