package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.util.List;

@Data
@Table(name = "tb_spec_group")
public class Specification {


    private Long id;
    private String name;
    @Id
    private  Long cid;
    @Transient
    private List<Specparam> params;

}