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


import java.util.*;

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
    public List<Specparam> queryparamByGid(Long gid,Long cid,String searching) {

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

    /*** 
    * @Description: 查询商品分类信息
    * @Param: [cid] 
    * @return: java.util.List<com.leyou.item.pojo.Specification> 
    * @Author: zhoukx
    * @Date: 2019/5/18 
    */
        public List<Specification> queryListByCid(Long cid) {
            //查询规格参数
            List<Specification> specifications = queryById(cid);

            //c查询规格类下参数
            List<Specparam> specparams = queryparamByGid(null, cid, null);
            // 先把规格参数变成map，毛的key是规格组的id，map 的值是组下的所有参数
            Map<Long, List<Specparam>> map = new HashMap<>();
            for (Specparam param : specparams) {
                if (!map.containsKey(param.getGroupId())) {
                    //这个组的id在map中不存在，新增一个list
                    map.put(param.getGroupId(),new ArrayList<>());
                }
                map.get(param.getGroupId()).add(param);
            }
            // 填充param 到group中
            for (Specification specGroup : specifications) {
                specGroup.setParams(map.get(specGroup.getId()));
            }
            return specifications;
        }
}