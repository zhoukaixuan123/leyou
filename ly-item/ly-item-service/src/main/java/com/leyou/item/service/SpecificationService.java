package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecificationMapper;
import com.leyou.item.mapper.SpecparamMapper;
import com.leyou.item.pojo.Specification;
import com.leyou.item.pojo.Specparam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.Collections;
import java.util.List;

@Service
public class SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;

    @Autowired
    private SpecparamMapper specparamMapper;

    public List<Specification> queryById(Long id) {
        Specification specification = new Specification();
        specification.setCid(id);

       List<Specification> speci=  this.specificationMapper.select(specification);
        if (CollectionUtils.isEmpty(speci)){
            throw  new LyException(ExceptionEnum.SELE_GROUP_NOT_FOND);
        }
        return speci;
    }


    /*** 
    * @Description:  根据分组信息  查出分组的规格
    * @Param: [gid] 
    * @return: java.lang.Object 
    * @Author: zhoukx
    * @Date: 2019/4/23 
    */ 
    public List<Specparam> queryparamByGid(Long gid,Long cid,Boolean searching) {

       Specparam specparam = new Specparam();
       specparam.setGroupId(gid);
       specparam.setCid(cid);
       specparam.setSearching(searching);
       //通用mapper 用法
        List<Specparam> params= specparamMapper.select(specparam);
        if (CollectionUtils.isEmpty(params)){
            throw  new LyException(ExceptionEnum.SELE_PARAM_NOT_FOND);
        }
        return  params;
    }
}