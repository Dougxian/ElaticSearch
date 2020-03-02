package com.example.demo.dao;

import com.example.demo.entity.Pages;

import java.util.List;

//自定义复杂方法不需要继承ElasticsearchRepository
public interface CustomPagesRepository {
    //term查询高亮(name),即多字段查询
    List<Pages> findByKeyAndHighlightAdnPageable(String keyword,int page,int size);

    //term 查询结果的数量
    Integer findCountByKey(String keyword);

    //分页查询所有
    List<Pages> findAll(Integer page, Integer size);

}
