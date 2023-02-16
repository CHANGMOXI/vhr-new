package org.changmoxi.vhr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Hr用户类
 */
@ToString
@EqualsAndHashCode
public class Hr implements UserDetails {
    private Integer id;

    private String name;

    private String phone;

    private String telephone;

    private String address;

    private Boolean enabled;

    private String username;

    private String password;

    private String avatar;

    private String remark;

    private Boolean deleted;

    /**
     * 用户所具备的角色
     */
    private List<Role> roles;

    /**
     * 账号是否没有过期
     *
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        //true: 没有过期
        return true;
    }

    /**
     * 账号是否没有被锁定
     *
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        //true: 没有被锁定
        return true;
    }

    /**
     * 密码是否没有过期
     *
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        //true: 没有过期
        return true;
    }

    /**
     * 是否可用
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        //返回自己的enabled字段
        return enabled;
    }

    /**
     * 返回用户所具备的角色
     * JsonIgnore注解: 向前端返回Hr数据时忽略authorities权限集合
     * 因为前端传Hr对象到后端时，如果不自定义反序列化的类，authorities权限集合反序列化会失败
     *
     * @return
     */
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(roles.size());
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}