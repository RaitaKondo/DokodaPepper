package com.example.demo.controller.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Comment;
import com.example.demo.entity.FoundIt;
import com.example.demo.entity.FoundItId;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostImage;
import com.example.demo.entity.Report;
import com.example.demo.entity.ReportId;
import com.example.demo.entity.User;
import com.example.demo.form.CommentForm;
import com.example.demo.form.CommentReturnForm;
import com.example.demo.form.PostForm;
import com.example.demo.form.PostReturnForm;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.FoundItRepository;
import com.example.demo.repository.PostImageRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.PrefectureRepository;
import com.example.demo.repository.ReportRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PostService;

@RestController
@RequestMapping("/api")
public class PostController {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CityRepository cityRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FoundItRepository foundItRepository;
    private final PostImageRepository postImageRepository;
    private final PrefectureRepository prefectureRepository;
    private final ReportRepository reportRepository;

    public PostController(PostService postService, CityRepository cityRepository, UserRepository userRepository,
            PostRepository postRepository, FoundItRepository foundItRepository, PostImageRepository postImageRepository,
            PrefectureRepository PrefectureRepository, ReportRepository reportRepository,
            CommentRepository commentRepository) {
        this.postService = postService;
        this.cityRepository = cityRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.foundItRepository = foundItRepository;
        this.postImageRepository = postImageRepository;
        this.prefectureRepository = PrefectureRepository;
        this.reportRepository = reportRepository;
        this.commentRepository = commentRepository;
    }

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

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentReturnForm>> getComments(@PathVariable Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtDesc(post);
        List<CommentReturnForm> commentReturnForms = comments.stream()
                .map((comment) -> new CommentReturnForm(comment.getId(), comment.getUser().getUsername(),
                        comment.getContent(), comment.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(commentReturnForms);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @Valid @RequestBody CommentForm form,
            Authentication authentication) {

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(form.getContent());
        comment.setUser(user);
        comment.setPost(post);

        commentRepository.save(comment);

        return ResponseEntity.ok("コメントを投稿しました");
    }

    @PostMapping("/posts/{postId}/edited")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editPost(@Validated @ModelAttribute PostForm postForm, Authentication authentication,
            @PathVariable Long postId) {
        // ユーザー情報を取得 疎結合性を維持するためにauthentication.getPrincipal()は使用しない。
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            // Postを作成
            Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
            if (!post.getUser().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("この投稿を編集する権限がありません。");
            }

            post.setContent(postForm.getContent());

            // Postを保存
            postRepository.save(post);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("投稿の作成に失敗しました: " + e.getMessage());
        }
    }

    @PostMapping("/posts/{postId}/delete")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editPost(Authentication authentication, @PathVariable Long postId) {
        // ユーザー情報を取得 疎結合性を維持するためにauthentication.getPrincipal()は使用しない。
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
            if (!post.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("この投稿を削除する権限がありません。");
            }
            postRepository.deleteById(postId);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("投稿の削除に失敗しました: " + e.getMessage());
        }
    }

    @GetMapping("/posts")
    public Page<PostReturnForm> getPosts(@RequestParam(defaultValue = "0") int page, Authentication authentication) {
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
            postReturnForm.setPrefectureId(post.getCity().getPrefecture().getId());

            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Post postish = postRepository.findById(post.getId())
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            FoundItId foundItId = new FoundItId(user.getId(), postish.getId());
            boolean isFoundIt = foundItRepository.existsById(foundItId);
            postReturnForm.setFoundIt(isFoundIt);

            ReportId reportId = new ReportId(user.getId(), postish.getId());
            boolean isReported = reportRepository.existsById(reportId);
            postReturnForm.setReported(isReported);

            postReturnForm.setNumberOfFoundIt(foundItRepository.countByPost_Id(post.getId()));
            postReturnForm.setNumberOfReported(reportRepository.countByPost_Id(post.getId()));

            return postReturnForm;
        });
        return postReturnForms;
    }

    @GetMapping("/posts/prefecture/{prefectureId}")
    public Page<PostReturnForm> getPostsByPref(@RequestParam(defaultValue = "0") int page,
            @PathVariable Long prefectureId, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "id"));
        Page<PostReturnForm> postReturnForms = postRepository.findByPrefectureId(prefectureId, pageable).map(post -> {
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
            postReturnForm.setPrefectureId(post.getCity().getPrefecture().getId());

            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Post postish = postRepository.findById(post.getId())
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            FoundItId foundItId = new FoundItId(user.getId(), postish.getId());
            boolean isFoundIt = foundItRepository.existsById(foundItId);
            postReturnForm.setFoundIt(isFoundIt);
            return postReturnForm;
        });
        return postReturnForms;
    }

    @GetMapping("/posts/prefecture/{prefectureId}/city/{cityId}")
    public Page<PostReturnForm> getPostsByPrefAndCity(@RequestParam(defaultValue = "0") int page,
            @PathVariable Long cityId, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "id"));
        Page<PostReturnForm> postReturnForms = postRepository.findByCity_Id(cityId, pageable).map(post -> {
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
            postReturnForm.setPrefectureId(post.getCity().getPrefecture().getId());
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Post postish = postRepository.findById(post.getId())
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            FoundItId foundItId = new FoundItId(user.getId(), postish.getId());
            boolean isFoundIt = foundItRepository.existsById(foundItId);
            postReturnForm.setFoundIt(isFoundIt);
            return postReturnForm;
        });
        return postReturnForms;
    }

    @PostMapping("/postNew")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createPost(@Validated @ModelAttribute PostForm postForm, Authentication authentication) {
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

            if (postForm.getPrefectureId() != null) {
                prefectureRepository.findById(postForm.getPrefectureId()).ifPresent(post::setPrefecture);
            }
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

                // postFormで受け取った時点ではimages[]になっている。リアクトから出した時点では複数の images ”バイナリー”で出る。
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

    @GetMapping("/posts/{postId}/found")
    public ResponseEntity<Long> getFoundCounter(@PathVariable Long postId) {
        Long foundCounter = foundItRepository.countByPost_Id(postId);
        return ResponseEntity.ok(foundCounter);
    }

    @PostMapping("posts/{postId}/found")
    public ResponseEntity<String> foundPepper(@PathVariable Long postId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        FoundItId foundItId = new FoundItId(user.getId(), post.getId());
        if (foundItRepository.existsById(foundItId)) {
            foundItRepository.deleteById(foundItId);
            return ResponseEntity.ok("found it removed");
        }

        FoundIt foundIt = new FoundIt(user, post);
        foundItRepository.save(foundIt);

        return ResponseEntity.ok("found it added");
    }

    @PostMapping("posts/{postId}/report")
    public ResponseEntity<String> reportPost(@PathVariable Long postId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        ReportId reportId = new ReportId(user.getId(), post.getId());

        if (reportRepository.existsById(reportId)) {
            reportRepository.deleteById(reportId);
            return ResponseEntity.ok("report removed");
        }

        Report report = new Report(user, post);
        reportRepository.save(report);
        return ResponseEntity.ok("report added");
    }

//    @GetMapping("/posts/{postId}/isFoundIt")
//    public ResponseEntity<Boolean> isFoundIt(@PathVariable Long postId, Authentication authentication) {
//        String username = authentication.getName();
//        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
//        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
//
//        FoundItId foundItId = new FoundItId(user.getId(), post.getId());
//        boolean isFoundIt = foundItRepository.existsById(foundItId);
//
//        return ResponseEntity.ok(isFoundIt);
//    }
}