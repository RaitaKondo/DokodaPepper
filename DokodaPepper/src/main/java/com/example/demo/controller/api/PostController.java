package com.example.demo.controller.api;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    
    @GetMapping("/getOne")
    public Post getPostById() {
        return postService.getSingle();
    }
    
    @GetMapping("/getForTop")
    public List<Post> getForTop() {
        return postService.getTop6Posts();
    }

    
    @GetMapping("/posts")
    public Page<Post> getPosts(@RequestParam(defaultValue = "0") int page) {
        return postService.getPosts(page);
    }
    
    
}
