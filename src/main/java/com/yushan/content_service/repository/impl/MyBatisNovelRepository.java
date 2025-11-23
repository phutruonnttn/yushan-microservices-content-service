package com.yushan.content_service.repository.impl;

import com.yushan.content_service.dao.CategoryMapper;
import com.yushan.content_service.dao.ChapterMapper;
import com.yushan.content_service.dao.NovelMapper;
import com.yushan.content_service.dto.novel.NovelSearchRequestDTO;
import com.yushan.content_service.entity.Novel;
import com.yushan.content_service.repository.NovelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * MyBatis implementation of NovelRepository.
 * Handles aggregate-level operations for Novel, Chapter, and Category.
 */
@Repository
public class MyBatisNovelRepository implements NovelRepository {
    
    @Autowired
    private NovelMapper novelMapper;
    
    @Autowired
    private ChapterMapper chapterMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Override
    public Novel findById(Integer id) {
        return novelMapper.selectByPrimaryKey(id);
    }
    
    @Override
    public Novel findByUuid(UUID uuid) {
        return novelMapper.selectByUuid(uuid);
    }
    
    @Override
    public Novel save(Novel novel) {
        if (novel.getId() == null) {
            // Insert new novel
            novelMapper.insertSelective(novel);
        } else {
            // Update existing novel
            novelMapper.updateByPrimaryKeySelective(novel);
        }
        return novel;
    }
    
    @Override
    public void delete(Integer id) {
        novelMapper.deleteByPrimaryKey(id);
    }
    
    @Override
    public Novel findNovelWithChapters(Integer novelId) {
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel != null) {
            // Load chapters for the novel (aggregate-level operation)
            // Note: Chapters are not directly stored in Novel entity,
            // but this method provides aggregate-level access
            // In a full DDD implementation, you might have a NovelWithChapters value object
            chapterMapper.selectByNovelId(novelId);
        }
        return novel;
    }
    
    @Override
    public Novel findNovelWithCategory(Integer novelId) {
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel != null && novel.getCategoryId() != null) {
            // Load category for the novel (aggregate-level operation)
            // Note: Category is not directly stored in Novel entity,
            // but this method provides aggregate-level access
            categoryMapper.selectByPrimaryKey(novel.getCategoryId());
        }
        return novel;
    }
    
    @Override
    public List<Novel> findNovelsWithPagination(NovelSearchRequestDTO request) {
        return novelMapper.selectNovelsWithPagination(request);
    }
    
    @Override
    public long countNovels(NovelSearchRequestDTO request) {
        return novelMapper.countNovels(request);
    }
    
    @Override
    public List<Novel> findAllNovelsWithPagination(NovelSearchRequestDTO request) {
        return novelMapper.selectAllNovelsWithPagination(request);
    }
    
    @Override
    public long countAllNovels(NovelSearchRequestDTO request) {
        return novelMapper.countAllNovels(request);
    }
    
    @Override
    public List<Novel> findByIds(List<Integer> ids) {
        return novelMapper.selectByIds(ids);
    }
    
    @Override
    public List<Novel> findByUuids(List<UUID> uuids) {
        return novelMapper.selectByUuids(uuids);
    }
    
    @Override
    public List<Novel> findNovelsByRanking(Integer categoryId, String sortType, int offset, int limit) {
        return novelMapper.selectNovelsByRanking(categoryId, sortType, offset, limit);
    }
    
    @Override
    public long countNovelsByRanking(Integer categoryId) {
        return novelMapper.countNovelsByRanking(categoryId);
    }
    
    @Override
    public void incrementViewCount(Integer novelId) {
        novelMapper.incrementViewCount(novelId);
    }
    
    @Override
    public void incrementVoteCount(Integer novelId) {
        novelMapper.incrementVoteCount(novelId);
    }
    
    @Override
    public void decrementVoteCount(Integer novelId) {
        novelMapper.decrementVoteCount(novelId);
    }
    
    @Override
    public void updateChapterCount(Integer novelId, Integer chapterCount) {
        novelMapper.updateChapterCount(novelId, chapterCount);
    }
    
    @Override
    public void updateWordCount(Integer novelId, Long wordCount) {
        novelMapper.updateWordCount(novelId, wordCount);
    }
    
    @Override
    public void updateRating(Integer novelId, Float avgRating, Integer reviewCount) {
        novelMapper.updateRating(novelId, avgRating, reviewCount);
    }
    
    @Override
    public void updateStatus(Integer novelId, Integer status) {
        novelMapper.updateStatus(novelId, status);
    }
    
    @Override
    public void updatePublishTime(Integer novelId, java.util.Date publishTime) {
        novelMapper.updatePublishTime(novelId, publishTime);
    }
    
    @Override
    public List<Novel> findNovelsUnderReview(int offset, int limit) {
        return novelMapper.selectNovelsUnderReview(offset, limit);
    }
    
    @Override
    public long countNovelsUnderReview() {
        return novelMapper.countNovelsUnderReview();
    }
}

