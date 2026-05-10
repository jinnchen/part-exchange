package org.example.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 
 * @TableName sys_menu
 */
@TableName(value ="sys_menu")
@Data
public class SysMenu {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 状态 0-无效 1-有效
     */
    private Integer status;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 修改人
     */
    private Long modifyBy;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 修改时间
     */
    private Date lastUpdateTime;

    /**
     * 上级菜单ID
     */
    private Long parentId;

    /**
     * 上级菜单唯一key
     */
    private String parentKey;

    /**
     * 类别 1-分类 2-节点
     */
    private Integer type;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 目标地址
     */
    private String targetUrl;

    /**
     * 排序索引
     */
    private Integer sort;

    @TableField(exist = false)
    private List<SysPrivilege> privileges = Collections.emptyList();

    @TableField(exist = false)
    private List<SysMenu> children = Collections.emptyList();

    public String getMenuKey() {
        if(!StringUtils.hasText(this.parentKey)){
            return this.parentKey+"."+this.id;
        }
        return this.id.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SysMenu other = (SysMenu) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreateBy() == null ? other.getCreateBy() == null : this.getCreateBy().equals(other.getCreateBy()))
            && (this.getModifyBy() == null ? other.getModifyBy() == null : this.getModifyBy().equals(other.getModifyBy()))
            && (this.getCreated() == null ? other.getCreated() == null : this.getCreated().equals(other.getCreated()))
            && (this.getLastUpdateTime() == null ? other.getLastUpdateTime() == null : this.getLastUpdateTime().equals(other.getLastUpdateTime()))
            && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()))
            && (this.getParentKey() == null ? other.getParentKey() == null : this.getParentKey().equals(other.getParentKey()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDesc() == null ? other.getDesc() == null : this.getDesc().equals(other.getDesc()))
            && (this.getTargetUrl() == null ? other.getTargetUrl() == null : this.getTargetUrl().equals(other.getTargetUrl()))
            && (this.getSort() == null ? other.getSort() == null : this.getSort().equals(other.getSort()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateBy() == null) ? 0 : getCreateBy().hashCode());
        result = prime * result + ((getModifyBy() == null) ? 0 : getModifyBy().hashCode());
        result = prime * result + ((getCreated() == null) ? 0 : getCreated().hashCode());
        result = prime * result + ((getLastUpdateTime() == null) ? 0 : getLastUpdateTime().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getParentKey() == null) ? 0 : getParentKey().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDesc() == null) ? 0 : getDesc().hashCode());
        result = prime * result + ((getTargetUrl() == null) ? 0 : getTargetUrl().hashCode());
        result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", status=").append(status);
        sb.append(", createBy=").append(createBy);
        sb.append(", modifyBy=").append(modifyBy);
        sb.append(", created=").append(created);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append(", parentId=").append(parentId);
        sb.append(", parentKey=").append(parentKey);
        sb.append(", type=").append(type);
        sb.append(", name=").append(name);
        sb.append(", desc=").append(desc);
        sb.append(", targetUrl=").append(targetUrl);
        sb.append(", sort=").append(sort);
        sb.append("]");
        return sb.toString();
    }
}