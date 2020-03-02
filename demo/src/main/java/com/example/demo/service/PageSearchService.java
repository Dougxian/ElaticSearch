package com.example.demo.service;

import com.example.demo.dao.CustomPagesRepository;
import com.example.demo.entity.Pages;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//对CustomPagesRepository接口的实现。
@Component
public class PageSearchService implements CustomPagesRepository {

    //实现类需要注入ElasticsearchTemplet
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    /*
    实现多字段检索，并高亮关键字
     */
    public List<Pages> findByKeyAndHighlightAdnPageable(String keyword, int page, int size) {
        //构建高亮对象，设置高亮格式
        HighlightBuilder.Field nameField = new HighlightBuilder
                .Field("*")
                .preTags("<span style='color:red'>")
                .postTags("</span>").requireFieldMatch(false);

        //多字段查询对象，可同时在title和content查询 对应实体类中的属性名
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))//构建搜索内容
                .withPageable(PageRequest.of(page - 1, size))//搜索结果的截取
                .withHighlightFields(nameField)//添加高亮格式
                .build();//返回创建完毕的NativeSearchQuery对象

        AggregatedPage<Pages> products = elasticsearchTemplate.//实例化一个AggregatedPage聚合结果对象，用elasticsearchTemplate的方法来初始化
                queryForPage(nativeSearchQuery, Pages.class, new SearchResultMapper() {//多层嵌套的函数体
                    @Override
                    public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                        SearchHits searchHits = response.getHits();
                        SearchHit[] hits = searchHits.getHits();
                        ArrayList<Pages> products = new ArrayList<Pages>();
                        for (SearchHit hit : hits) {
                            Pages product = new Pages();
                            //原始map，创建映射
                            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                            product.setUrl(sourceAsMap.get("url").toString());
                            product.setTitle(sourceAsMap.get("title").toString());
                            product.setContent(sourceAsMap.get("content").toString());

                            //高亮
                            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                            System.out.println(highlightFields);
                            if (highlightFields.get("title") != null) {
                                String nameHighlight = highlightFields.get("title").getFragments()[0].toString();
                                product.setTitle(nameHighlight);
                            }
                            if (highlightFields.get("content") != null) {
                                String contentHighlight = highlightFields.get("content").getFragments()[0].toString();
                                product.setContent(contentHighlight);
                            }
                            products.add(product);
                        }
                        return new AggregatedPageImpl<T>((List<T>) products);
                    }
                });
        return products.getContent();//返回聚合结果集中的数据
    }

    @Override
    public Integer findCountByKey(String keyword) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title","content"))
                .build();
        return elasticsearchTemplate.queryForList(nativeSearchQuery, Pages.class).size();
    }

    @Override
    //分页查所有
    public List<Pages> findAll(Integer page, Integer size) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withPageable(PageRequest.of(page-1, size))
                .build();
        //默认只查出来10条
        List<Pages> products = elasticsearchTemplate.queryForList(searchQuery, Pages.class);
        return products;

    }



}