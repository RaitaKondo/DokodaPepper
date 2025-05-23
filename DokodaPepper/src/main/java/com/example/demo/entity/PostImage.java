package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
@Table(name = "post_images")
public class PostImage {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@ManyToOne
@JoinColumn(name = "post_id", nullable = false)
@JsonIgnore   // Ignore this field in JSON serialization to avoid circular reference issues. データ取得時に親が子を、子が親を参照するため、無限ループになるのを防ぐ。JPA, hibernetで起きやすいエラー
private Post post;
//@ManyToOne + @JoinColumn を使って Post を直接受け取るのは、JPAのリレーション管理を最大限活用するためです。「ID」だけを持つとオブジェクト指向の利点を活かせない、"ただのRDB的" な設計になってしまいます。

@Column(name = "image_url", nullable = false)
private String imageUrl;

@Column(name = "sort_order")
private Integer sortOrder = 0;

@Column(name = "created_at", updatable = false)
@CreationTimestamp
private LocalDateTime createdAt;

@Column(name = "updated_at")
@UpdateTimestamp
private LocalDateTime updatedAt;

}
