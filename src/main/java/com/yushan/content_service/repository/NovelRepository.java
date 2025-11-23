package com.yushan.content_service.repository;

import com.yushan.content_service.dto.novel.NovelSearchRequestDTO;
import com.yushan.content_service.entity.Novel;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Novel aggregate.
 * Abstracts data access operations for Novel and its related entities (Chapter, Category).
 */
public interface NovelRepository {
    
    // Basic CRUD operations
    Novel findById(Integer id);
    
    Novel findByUuid(UUID uuid);
    
    Novel save(Novel novel);
    
    void delete(Integer id);
    
    // Aggregate-level queries
    /**
     * Find novel with all its chapters and category
     */
    Novel findNovelWithChapters(Integer novelId);
    
    /**
     * Find novel with category information
     */
    Novel findNovelWithCategory(Integer novelId);
    
    // Search and pagination
    List<Novel> findNovelsWithPagination(NovelSearchRequestDTO request);
    
    long countNovels(NovelSearchRequestDTO request);
    
    // Admin queries (including ARCHIVED)
    List<Novel> findAllNovelsWithPagination(NovelSearchRequestDTO request);
    
    long countAllNovels(NovelSearchRequestDTO request);
    
    // Batch operations
    List<Novel> findByIds(List<Integer> ids);
    
    List<Novel> findByUuids(List<UUID> uuids);
    
    // Ranking queries
    List<Novel> findNovelsByRanking(Integer categoryId, String sortType, int offset, int limit);
    
    long countNovelsByRanking(Integer categoryId);
    
    // Statistics and counter operations
    void incrementViewCount(Integer novelId);
    
    void incrementVoteCount(Integer novelId);
    
    void decrementVoteCount(Integer novelId);
    
    void updateChapterCount(Integer novelId, Integer chapterCount);
    
    void updateWordCount(Integer novelId, Long wordCount);
    
    void updateRating(Integer novelId, Float avgRating, Integer reviewCount);
    
    void updateStatus(Integer novelId, Integer status);
    
    void updatePublishTime(Integer novelId, java.util.Date publishTime);
    
    // Admin statistics
    List<Novel> findNovelsUnderReview(int offset, int limit);
    
    long countNovelsUnderReview();
}

