package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

//添加索引库
public interface GoodsRepository  extends ElasticsearchRepository<Goods, Long> {
}
