package com.leyou.item.pojo;

import lombok.Data;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/14$
 * @description 测试实体类$
 */
@Data
public class Item {

    private String user_name;
    private String study_id;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getStudy_id() {
        return study_id;
    }

    public void setStudy_id(String study_id) {
        this.study_id = study_id;
    }
}
