package com.example.demo.controller.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Post;
import com.example.demo.entity.PostImage;
import com.example.demo.entity.User;
import com.example.demo.form.PostForm;
import com.example.demo.form.PostReturnForm;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.FoundItRepository;
import com.example.demo.repository.PostImageRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PostService;

@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;
    private final CityRepository cityRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FoundItRepository foundItRepository;
    private final PostImageRepository postImageRepository;

    public PostController(PostService postService, CityRepository cityRepository, UserRepository userRepository,
            PostRepository postRepository, FoundItRepository foundItRepository,
            PostImageRepository postImageRepository) {
        this.postService = postService;
        this.cityRepository = cityRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.foundItRepository = foundItRepository;
        this.postImageRepository = postImageRepository;
    }

//    @GetMapping("/test")
//    public String test() {
//        return "test test";
//    }
//
//    @GetMapping("/all")
//    public List<Post> getAllPost() {
//        return postService.getAllPosts();
//    }
//
//    @GetMapping("/getOne")
//    public Post getPostById() {
//        return postService.getSingle();
//    }
//
//    @GetMapping("/getForTop")
//    public List<Post> getForTop() {
//        return postService.getTop6Posts();
//    }

//    @GetMapping("/posts")
//    public Page<Post> getPosts(@RequestParam(defaultValue = "0") int page) {
//        Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "id"));
//        return postService.getPosts(pageable);
//    }
    
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostReturnForm> getPostById(@RequestParam Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Post post = postOpt.get();
        PostReturnForm postReturnForm = new PostReturnForm();
        postReturnForm.setPostId(post.getId());
        postReturnForm.setContent(post.getContent());
        postReturnForm.setCreatedAt(post.getCreatedAt());
        postReturnForm.setUpdatedAt(post.getUpdatedAt());
        postReturnForm.setUserName(post.getUser().getUsername());
        postReturnForm.setCity(post.getCity());
        postReturnForm.setImages(post.getImages());
        postReturnForm.setPrefectureName(post.getPrefectureName());
        postReturnForm.setLatitude(post.getLatitude());
        postReturnForm.setLongitude(post.getLongitude());
        postReturnForm.setAddress(post.getAddress());

        return ResponseEntity.ok(postReturnForm);
    }

    @GetMapping("/posts")
    public Page<PostReturnForm> getPosts(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "id"));
        Page<PostReturnForm> postReturnForms = postRepository.findAll(pageable).map(post -> {
            PostReturnForm postReturnForm = new PostReturnForm();
            postReturnForm.setPostId(post.getId());
            postReturnForm.setContent(post.getContent());
            postReturnForm.setCreatedAt(post.getCreatedAt());
            postReturnForm.setUpdatedAt(post.getUpdatedAt());
            postReturnForm.setUserName(post.getUser().getUsername());
            postReturnForm.setCity(post.getCity());
            postReturnForm.setImages(post.getImages());
            postReturnForm.setPrefectureName(post.getPrefectureName());
            postReturnForm.setLatitude(post.getLatitude());
            postReturnForm.setLongitude(post.getLongitude());
            postReturnForm.setAddress(post.getAddress());

            return postReturnForm;
        });
        return postReturnForms;
    }

    @PostMapping("/postNew")
    @Transactional
    public ResponseEntity<?> createPost( @Validated @ModelAttribute PostForm postForm, Authentication authentication) {
        // ユーザー情報を取得 疎結合性を維持するためにauthentication.getPrincipal()は使用しない。
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            System.out.println("PostForm: " + postForm);

            // Postを作成
            Post post = new Post();
            post.setContent(postForm.getContent());
            post.setLatitude(postForm.getLatitude());
            post.setLongitude(postForm.getLongitude());
            post.setAddress(postForm.getAddress());
            post.setUser(userOpt.get());

            if (postForm.getCityId() != null) {
                cityRepository.findById(postForm.getCityId()).ifPresent(post::setCity);
            }

            // Postを保存
            Post savedPost = postRepository.save(post);

            int order = 1; // 画像の順序を管理するための変数
            // 画像を保存

            if (postForm.getImages() == null || postForm.getImages().isEmpty()) {
                PostImage postImage = new PostImage();
                postImage.setPost(savedPost);
                postImage.setImageUrl("/images/default.jpg"); // デフォルト画像を設定");
                postImage.setSortOrder(1);
                postImageRepository.save(postImage);
            } else {

                for (MultipartFile image : postForm.getImages()) {
                    String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                    Path path = Paths.get("src/main/resources/static/images", fileName);
                    Files.copy(image.getInputStream(), path);

                    PostImage postImage = new PostImage();
                    postImage.setPost(savedPost);
                    postImage.setImageUrl("/images/" + fileName);
                    postImage.setSortOrder(order++);
                    postImageRepository.save(postImage);
                }
            }

            return ResponseEntity.ok("ok");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("画像の保存中にエラーが発生しました: " + e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("投稿の作成に失敗しました: " + e.getMessage());
        }
    }

//    @GetMapping("/my-posts")
//    public ResponseEntity<List<Post>> getMyPosts(Authentication authentication) {
//        String username = authentication.getName();
//        Optional<User> userOpt = userRepository.findByUsername(username);
//        if (userOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        List<Post> posts = postRepository.findByUser(userOpt.get());
//        return ResponseEntity.ok(posts);
//    }
//
//    @GetMapping("/liked")
//    public ResponseEntity<List<Post>> getLikedPosts(Authentication authentication) {
//        String username = authentication.getName();
//        Optional<User> userOpt = userRepository.findByUsername(username);
//        if (userOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        List<Post> likedPosts = foundItRepository.findPostsByUserId(userOpt.get().getId());
//        return ResponseEntity.ok(likedPosts);
//    }

}
