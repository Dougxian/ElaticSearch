package com.example.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import sun.util.resources.ga.LocaleNames_ga;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;

@Document(indexName = "sample",type = "demo",shards = 5,replicas = 1)
public class Pages {

    @Id
    @Field(index = false)
    private String id;
    @Field(index = false)
    private String url;

    private String title;

    private String content;

    public void setID(String id){this.id = id;}
    public String getID(){return id;}

    public void setUrl(String url){this.url = url;}
    public String getUrl(){return url;}

    public void setTitle(String title){this.title = title;}
    public String getTitle(){return title;}

    public void setContent(String content){this.content = content;}
    public String getContent(){return content;}


}
