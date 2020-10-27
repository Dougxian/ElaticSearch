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
    //调试接口
    @PostMapping("/main")
    public void search(@RequestBody String keyword){
        List<Pages> PageList = customPagesRepository.findByKeyAndHighlightAdnPageable("Spring Boot is a very good skills",1,5);
        for (Pages item :PageList
             ) {
            System.out.println(item.getTitle()+item.getContent());
        }
    }

}
