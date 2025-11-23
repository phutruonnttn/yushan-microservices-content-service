package com.yushan.content_service.repository.impl;

import com.yushan.content_service.dao.ChapterMapper;
import com.yushan.content_service.dto.chapter.ChapterSearchRequestDTO;
import com.yushan.content_service.entity.Chapter;
import com.yushan.content_service.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class MyBatisChapterRepository implements ChapterRepository {

    private final ChapterMapper chapterMapper;

    @Autowired
    public MyBatisChapterRepository(ChapterMapper chapterMapper) {
        this.chapterMapper = chapterMapper;
    }

    @Override
    public Chapter findById(Integer id) {
        return chapterMapper.selectByPrimaryKey(id);
    }

    @Override
    public Chapter findByUuid(UUID uuid) {
        return chapterMapper.selectByUuid(uuid);
    }

    @Override
    public Chapter save(Chapter chapter) {
        if (chapter.getId() == null) {
            chapterMapper.insertSelective(chapter);
        } else {
            chapterMapper.updateByPrimaryKeySelective(chapter);
        }
        return chapter;
    }

    @Override
    public void delete(Integer id) {
        chapterMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        // If there's a method for this, use it, otherwise find by uuid first then delete
        Chapter chapter = chapterMapper.selectByUuid(uuid);
        if (chapter != null) {
            chapterMapper.deleteByPrimaryKey(chapter.getId());
        }
    }

    @Override
    public List<Chapter> findByIds(List<Integer> ids) {
        return chapterMapper.selectByIds(ids);
    }

    @Override
    public List<Chapter> findByNovelId(Integer novelId) {
        return chapterMapper.selectByNovelId(novelId);
    }

    @Override
    public List<Chapter> findPublishedByNovelId(Integer novelId) {
        return chapterMapper.selectPublishedByNovelId(novelId);
    }

    @Override
    public List<Chapter> findByNovelIdWithPagination(Integer novelId, int offset, int limit) {
        return chapterMapper.selectByNovelIdWithPagination(novelId, offset, limit);
    }

    @Override
    public List<Chapter> findPublishedByNovelIdWithPagination(Integer novelId, int offset, int limit) {
        return chapterMapper.selectPublishedByNovelIdWithPagination(novelId, offset, limit);
    }

    @Override
    public long countByNovelId(Integer novelId) {
        return chapterMapper.countByNovelId(novelId);
    }

    @Override
    public long countPublishedByNovelId(Integer novelId) {
        return chapterMapper.countPublishedByNovelId(novelId);
    }

    @Override
    public List<Chapter> findPublishedChapters() {
        return chapterMapper.selectPublishedChapters();
    }

    @Override
    public Chapter findNextChapter(Integer novelId, Integer chapterNumber) {
        return chapterMapper.selectNextChapter(novelId, chapterNumber);
    }

    @Override
    public Chapter findPreviousChapter(Integer novelId, Integer chapterNumber) {
        return chapterMapper.selectPreviousChapter(novelId, chapterNumber);
    }

    @Override
    public Chapter findByNovelIdAndChapterNumber(Integer novelId, Integer chapterNumber) {
        return chapterMapper.selectByNovelIdAndChapterNumber(novelId, chapterNumber);
    }

    @Override
    public void incrementViewCount(Integer id) {
        chapterMapper.incrementViewCount(id);
    }

    @Override
    public boolean existsByNovelIdAndChapterNumber(Integer novelId, Integer chapterNumber) {
        return chapterMapper.existsByNovelIdAndChapterNumber(novelId, chapterNumber);
    }

    @Override
    public Integer findMaxChapterNumberByNovelId(Integer novelId) {
        return chapterMapper.selectMaxChapterNumberByNovelId(novelId);
    }

    @Override
    public void batchInsert(List<Chapter> chapters) {
        chapterMapper.batchInsert(chapters);
    }

    @Override
    public void softDelete(Integer id) {
        chapterMapper.softDeleteByPrimaryKey(id);
    }

    @Override
    public void softDeleteByUuid(UUID uuid) {
        chapterMapper.softDeleteByUuid(uuid);
    }

    @Override
    public List<Chapter> findDraftsByNovelId(Integer novelId) {
        return chapterMapper.selectDraftsByNovelId(novelId);
    }

    @Override
    public List<Chapter> findScheduledByNovelId(Integer novelId) {
        return chapterMapper.selectScheduledByNovelId(novelId);
    }

    @Override
    public long sumWordCountByNovelId(Integer novelId) {
        return chapterMapper.sumWordCountByNovelId(novelId);
    }

    @Override
    public long sumPublishedWordCountByNovelId(Integer novelId) {
        return chapterMapper.sumPublishedWordCountByNovelId(novelId);
    }

    @Override
    public void updatePublishStatusByIds(List<Integer> ids, Boolean isValid) {
        chapterMapper.updatePublishStatusByIds(ids, isValid);
    }

    @Override
    public List<Chapter> findChaptersWithSearch(ChapterSearchRequestDTO req) {
        return chapterMapper.selectChaptersWithSearch(req);
    }

    @Override
    public long countChaptersWithSearch(ChapterSearchRequestDTO req) {
        return chapterMapper.countChaptersWithSearch(req);
    }
}

