package com.example.demo.controller.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Post;
import com.example.demo.service.PostService;

@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;
    
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/test")
    public String test(){
        return "test test";
    }
    
    @GetMapping("/all")
    public List<Post> getAllPost(){
        return postService.getAllPosts();
    }

}
