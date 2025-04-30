package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }
    
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Post not found"));
    }
    
    public Post createPost(Post post) {
        return postRepository.save(post);
    }
    
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        postRepository.delete(post);
                
    }
}
