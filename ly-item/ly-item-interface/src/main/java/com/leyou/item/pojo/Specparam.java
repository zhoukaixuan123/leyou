package com.leyou.item.pojo;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/23$
 * @description 规格参数实体列$
 */


import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_spec_param")
public class Specparam {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;
    @Column(name = "`numeric`")
    private Boolean numeric;
    private String unit;
    private Boolean generic;
    private  Boolean searching;
    private String segments;


}
