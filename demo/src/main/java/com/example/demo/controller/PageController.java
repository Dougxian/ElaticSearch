package com.example.demo.controller;


import com.example.demo.dao.CustomPagesRepository;
import com.example.demo.entity.Pages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class PageController {

    @Autowired
    private CustomPagesRepository customPagesRepository;

    //使用postman调试接口，没有做显示页面
    @GetMapping("/main")
    public String showmenu(){
        return "main";
    }

    @PostMapping("/main")
    public void search(@RequestBody String keyword){
        List<Pages> l = customPagesRepository.findByKeyAndHighlightAdnPageable("Spring Boot is a very good skills",1,5);
        for (Pages item :l
             ) {
            System.out.println(item.getTitle()+item.getContent());
        }
    }

}
