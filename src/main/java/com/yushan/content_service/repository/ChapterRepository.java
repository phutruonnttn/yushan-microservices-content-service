package com.yushan.content_service.repository;

import com.yushan.content_service.dto.chapter.ChapterSearchRequestDTO;
import com.yushan.content_service.entity.Chapter;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Chapter aggregate.
 * Abstracts data access operations for Chapter entity.
 */
public interface ChapterRepository {
    
    // Basic CRUD operations
    Chapter findById(Integer id);
    
    Chapter findByUuid(UUID uuid);
    
    Chapter save(Chapter chapter);
    
    void delete(Integer id);
    
    void deleteByUuid(UUID uuid);
    
    // Query operations
    List<Chapter> findByIds(List<Integer> ids);
    
    List<Chapter> findByNovelId(Integer novelId);
    
    List<Chapter> findPublishedByNovelId(Integer novelId);
    
    List<Chapter> findByNovelIdWithPagination(Integer novelId, int offset, int limit);
    
    List<Chapter> findPublishedByNovelIdWithPagination(Integer novelId, int offset, int limit);
    
    long countByNovelId(Integer novelId);
    
    long countPublishedByNovelId(Integer novelId);
    
    List<Chapter> findPublishedChapters();
    
    // Chapter navigation
    Chapter findNextChapter(Integer novelId, Integer chapterNumber);
    
    Chapter findPreviousChapter(Integer novelId, Integer chapterNumber);
    
    Chapter findByNovelIdAndChapterNumber(Integer novelId, Integer chapterNumber);
    
    // View count management
    void incrementViewCount(Integer id);
    
    // Existence checks
    boolean existsByNovelIdAndChapterNumber(Integer novelId, Integer chapterNumber);
    
    // Max chapter number
    Integer findMaxChapterNumberByNovelId(Integer novelId);
    
    // Batch operations
    void batchInsert(List<Chapter> chapters);
    
    // Soft delete
    void softDelete(Integer id);
    
    void softDeleteByUuid(UUID uuid);
    
    // Author/Admin queries
    List<Chapter> findDraftsByNovelId(Integer novelId);
    
    List<Chapter> findScheduledByNovelId(Integer novelId);
    
    // Statistics
    long sumWordCountByNovelId(Integer novelId);
    
    long sumPublishedWordCountByNovelId(Integer novelId);
    
    // Bulk status updates
    void updatePublishStatusByIds(List<Integer> ids, Boolean isValid);
    
    // Dynamic search
    List<Chapter> findChaptersWithSearch(ChapterSearchRequestDTO req);
    
    long countChaptersWithSearch(ChapterSearchRequestDTO req);
}

