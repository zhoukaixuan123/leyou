package com.leyou.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Table(name = "tb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*** 
    * @Description: // 用户名 
    * @Param:  
    * @return:  
    * @Author: zhoukx
    * @Date: 2019/5/26 
    */
    /*非空  使用 Hibnain valitonl*/
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 4,max = 32,message ="用户名长度为" )
    private String username;

    /*** 
    * @Description: // 密码 
    * @Param:  
    * @return:  
    * @Author: zhoukx
    * @Date: 2019/5/26 
    */ 
    @JsonIgnore
    private String password;

    /*** 
    * @Description: // 电话 
    * @Param:  
    * @return:  
    * @Author: zhoukx
    * @Date: 2019/5/26 
    */
    /*校验手机号注解*/
    @Pattern(regexp = "^[0-9]{11}$",message = "手机号不正确")
    private String phone;

    /*** 
    * @Description: // 创建时间 
    * @Param:  
    * @return:  
    * @Author: zhoukx
    * @Date: 2019/5/26 
    */ 
    private Date created;

    /*** 
    * @Description: // 密码的盐值
    * @Param:  
    * @return:  
    * @Author: zhoukx
    * @Date: 2019/5/26 
    */ 
    @JsonIgnore
    private String salt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}