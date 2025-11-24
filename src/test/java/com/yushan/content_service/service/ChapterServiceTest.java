package com.yushan.content_service.service;

import com.yushan.content_service.domain.event.ChapterDomainEventPublisher;
import com.yushan.content_service.repository.ChapterRepository;
import org.springframework.test.util.ReflectionTestUtils;
import com.yushan.content_service.dto.chapter.*;
import com.yushan.content_service.dto.common.PageResponseDTO;
import com.yushan.content_service.entity.Chapter;
import com.yushan.content_service.entity.Novel;
import com.yushan.content_service.exception.ResourceNotFoundException;
import com.yushan.content_service.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Simple unit tests for ChapterService with mocked dependencies.
 */
public class ChapterServiceTest {

    private ChapterRepository chapterRepository;
    private RedisUtil redisUtil;
    private KafkaEventProducerService kafkaEventProducerService;
    private NovelService novelService;
    private ElasticsearchAutoIndexService elasticsearchAutoIndexService;
    private ChapterDomainEventPublisher chapterDomainEventPublisher;
    private ChapterService chapterService;

    @BeforeEach
    void setUp() {
        chapterRepository = Mockito.mock(ChapterRepository.class);
        redisUtil = Mockito.mock(RedisUtil.class);
        kafkaEventProducerService = Mockito.mock(KafkaEventProducerService.class);
        novelService = Mockito.mock(NovelService.class);
        elasticsearchAutoIndexService = Mockito.mock(ElasticsearchAutoIndexService.class);
        chapterDomainEventPublisher = Mockito.mock(ChapterDomainEventPublisher.class);

        chapterService = new ChapterService();
        // Inject dependencies using ReflectionTestUtils
        ReflectionTestUtils.setField(chapterService, "chapterRepository", chapterRepository);
        ReflectionTestUtils.setField(chapterService, "redisUtil", redisUtil);
        ReflectionTestUtils.setField(chapterService, "kafkaEventProducerService", kafkaEventProducerService);
        ReflectionTestUtils.setField(chapterService, "novelService", novelService);
        ReflectionTestUtils.setField(chapterService, "elasticsearchAutoIndexService", elasticsearchAutoIndexService);
        ReflectionTestUtils.setField(chapterService, "chapterDomainEventPublisher", chapterDomainEventPublisher);
    }

    @Test
    void testGetChapterByUuid_Success() {
        // Given
        UUID chapterUuid = UUID.randomUUID();
        Chapter chapter = new Chapter();
        chapter.setId(1);
        chapter.setUuid(chapterUuid);
        chapter.setNovelId(1);
        chapter.setChapterNumber(1);
        chapter.setTitle("Test Chapter");
        chapter.setContent("Test content");
        chapter.setWordCnt(10);
        chapter.setIsPremium(false);
        chapter.setYuanCost(0.0f);
        chapter.setIsValid(true);
        chapter.setCreateTime(new Date());
        chapter.setUpdateTime(new Date());
        chapter.setPublishTime(new Date());

        when(chapterRepository.findByUuid(chapterUuid)).thenReturn(chapter);

        // When
        ChapterDetailResponseDTO result = chapterService.getChapterByUuid(chapterUuid);

        // Then
        assertNotNull(result);
        assertEquals("Test Chapter", result.getTitle());
        assertEquals("Test content", result.getContent());
        assertEquals(10, result.getWordCnt());

        verify(chapterRepository).findByUuid(chapterUuid);
    }

    @Test
    void testGetChapterByUuid_NotFound() {
        // Given
        UUID chapterUuid = UUID.randomUUID();

        when(chapterRepository.findByUuid(chapterUuid)).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            chapterService.getChapterByUuid(chapterUuid);
        });

        verify(chapterRepository).findByUuid(chapterUuid);
    }

    @Test
    void testGetChapterStatistics_Success() {
        // Given
        Integer novelId = 1;

        Chapter chapter1 = new Chapter();
        chapter1.setId(1);
        chapter1.setUuid(UUID.randomUUID());
        chapter1.setNovelId(1);
        chapter1.setChapterNumber(1);
        chapter1.setTitle("Chapter 1");
        chapter1.setContent("Content 1");
        chapter1.setWordCnt(10);
        chapter1.setIsPremium(false);
        chapter1.setYuanCost(0.0f);
        chapter1.setViewCnt(100L);
        chapter1.setIsValid(true);
        chapter1.setCreateTime(new Date());
        chapter1.setUpdateTime(new Date());
        chapter1.setPublishTime(new Date());

        Chapter chapter2 = new Chapter();
        chapter2.setId(2);
        chapter2.setUuid(UUID.randomUUID());
        chapter2.setNovelId(1);
        chapter2.setChapterNumber(2);
        chapter2.setTitle("Chapter 2");
        chapter2.setContent("Content 2");
        chapter2.setWordCnt(15);
        chapter2.setIsPremium(true);
        chapter2.setYuanCost(0.025f); // 0.025 * 200 = 5.0f total revenue
        chapter2.setViewCnt(200L);
        chapter2.setIsValid(true);
        chapter2.setCreateTime(new Date());
        chapter2.setUpdateTime(new Date());
        chapter2.setPublishTime(new Date());

        List<Chapter> chapters = Arrays.asList(chapter1, chapter2);

        Novel novel = new Novel();
        novel.setId(1);
        novel.setTitle("Test Novel");
        novel.setStatus(1); // ACTIVE

        when(novelService.getNovelEntity(novelId)).thenReturn(novel);
        when(chapterRepository.findByNovelId(novelId)).thenReturn(chapters);
        when(chapterRepository.countByNovelId(novelId)).thenReturn(2L);
        when(chapterRepository.countPublishedByNovelId(novelId)).thenReturn(1L);
        when(chapterRepository.findDraftsByNovelId(novelId)).thenReturn(Arrays.asList(chapter1));
        when(chapterRepository.findScheduledByNovelId(novelId)).thenReturn(new ArrayList<>());
        when(chapterRepository.sumWordCountByNovelId(novelId)).thenReturn(25L);

        // When
        ChapterStatisticsResponseDTO result = chapterService.getChapterStatistics(novelId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalChapters());
        assertEquals(25, result.getTotalWordCount());
        assertEquals(300L, result.getTotalViewCount());
        assertEquals(5.0f, result.getTotalRevenue());
        assertEquals(1, result.getFreeChapters());
        assertEquals(1, result.getPremiumChapters());

        verify(novelService).getNovelEntity(novelId);
        verify(chapterRepository).findByNovelId(novelId);
    }

    @Test
    void testCreateChapter_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        ChapterCreateRequestDTO requestDTO = new ChapterCreateRequestDTO();
        requestDTO.setNovelId(1);
        requestDTO.setChapterNumber(1);
        requestDTO.setTitle("New Chapter");
        requestDTO.setContent("New chapter content");
        requestDTO.setWordCnt(100);
        requestDTO.setIsPremium(false);
        requestDTO.setYuanCost(0.0f);

        Novel novel = new Novel();
        novel.setId(1);
        novel.setTitle("Test Novel");
        novel.setAuthorId(userId);
        novel.setStatus(1); // ACTIVE

        Chapter createdChapter = new Chapter();
        createdChapter.setId(1);
        createdChapter.setUuid(UUID.randomUUID());
        createdChapter.setNovelId(1);
        createdChapter.setChapterNumber(1);
        createdChapter.setTitle("New Chapter");
        createdChapter.setContent("New chapter content");
        createdChapter.setWordCnt(100);
        createdChapter.setIsPremium(false);
        createdChapter.setYuanCost(0.0f);
        createdChapter.setIsValid(true);
        createdChapter.setCreateTime(new Date());
        createdChapter.setUpdateTime(new Date());
        createdChapter.setPublishTime(new Date());

        when(novelService.getNovelEntity(requestDTO.getNovelId())).thenReturn(novel);
        when(chapterRepository.existsByNovelIdAndChapterNumber(requestDTO.getNovelId(), requestDTO.getChapterNumber())).thenReturn(false);
        when(chapterRepository.save(any(Chapter.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(chapterRepository.findByUuid(any(UUID.class))).thenReturn(createdChapter);
        doNothing().when(redisUtil).invalidateChapterCaches(any(Integer.class));
        doNothing().when(kafkaEventProducerService).publishChapterCreatedEvent(any(Chapter.class), any(Novel.class), any(UUID.class));

        // When
        ChapterDetailResponseDTO result = chapterService.createChapter(userId, requestDTO);

        // Then
        assertNotNull(result);
        assertEquals("New Chapter", result.getTitle());
        assertEquals("New chapter content", result.getContent());
        assertEquals(100, result.getWordCnt());
        assertEquals(false, result.getIsPremium());
        assertEquals(0.0f, result.getYuanCost());

        verify(novelService).getNovelEntity(requestDTO.getNovelId());
        verify(chapterRepository).existsByNovelIdAndChapterNumber(requestDTO.getNovelId(), requestDTO.getChapterNumber());
        verify(chapterRepository).save(any(Chapter.class));
    }

    @Test
    void testCreateChapter_NovelNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        ChapterCreateRequestDTO requestDTO = new ChapterCreateRequestDTO();
        requestDTO.setNovelId(999);
        requestDTO.setChapterNumber(1);
        requestDTO.setTitle("New Chapter");
        requestDTO.setContent("New chapter content");
        requestDTO.setWordCnt(100);

        when(novelService.getNovelEntity(requestDTO.getNovelId())).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            chapterService.createChapter(userId, requestDTO);
        });

        verify(novelService).getNovelEntity(requestDTO.getNovelId());
    }

    @Test
    void testCreateChapter_NotAuthor() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID differentUserId = UUID.randomUUID();
        ChapterCreateRequestDTO requestDTO = new ChapterCreateRequestDTO();
        requestDTO.setNovelId(1);
        requestDTO.setChapterNumber(1);
        requestDTO.setTitle("New Chapter");
        requestDTO.setContent("New chapter content");
        requestDTO.setWordCnt(100);

        Novel novel = new Novel();
        novel.setId(1);
        novel.setTitle("Test Novel");
        novel.setAuthorId(differentUserId); // Different author
        novel.setStatus(1); // ACTIVE

        when(novelService.getNovelEntity(requestDTO.getNovelId())).thenReturn(novel);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            chapterService.createChapter(userId, requestDTO);
        });

        verify(novelService).getNovelEntity(requestDTO.getNovelId());
    }

    @Test
    void testCreateChapter_ChapterNumberExists() {
        // Given
        UUID userId = UUID.randomUUID();
        ChapterCreateRequestDTO requestDTO = new ChapterCreateRequestDTO();
        requestDTO.setNovelId(1);
        requestDTO.setChapterNumber(1);
        requestDTO.setTitle("New Chapter");
        requestDTO.setContent("New chapter content");
        requestDTO.setWordCnt(100);

        Novel novel = new Novel();
        novel.setId(1);
        novel.setTitle("Test Novel");
        novel.setAuthorId(userId);
        novel.setStatus(1); // ACTIVE

        when(novelService.getNovelEntity(requestDTO.getNovelId())).thenReturn(novel);
        when(chapterRepository.existsByNovelIdAndChapterNumber(requestDTO.getNovelId(), requestDTO.getChapterNumber())).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            chapterService.createChapter(userId, requestDTO);
        });

        verify(novelService).getNovelEntity(requestDTO.getNovelId());
        verify(chapterRepository).existsByNovelIdAndChapterNumber(requestDTO.getNovelId(), requestDTO.getChapterNumber());
    }

    @Test
    void testUpdateChapter_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID chapterUuid = UUID.randomUUID();
        ChapterUpdateRequestDTO requestDTO = new ChapterUpdateRequestDTO();
        requestDTO.setUuid(chapterUuid);
        requestDTO.setTitle("Updated Chapter");
        requestDTO.setContent("Updated chapter content");
        requestDTO.setWordCnt(150);

        Chapter existingChapter = new Chapter();
        existingChapter.setId(1);
        existingChapter.setUuid(chapterUuid);
        existingChapter.setNovelId(1);
        existingChapter.setChapterNumber(1);
        existingChapter.setTitle("Original Chapter");
        existingChapter.setContent("Original content");
        existingChapter.setWordCnt(100);
        existingChapter.setIsPremium(false);
        existingChapter.setYuanCost(0.0f);
        existingChapter.setIsValid(true);
        existingChapter.setCreateTime(new Date());
        existingChapter.setUpdateTime(new Date());
        existingChapter.setPublishTime(new Date());

        Novel novel = new Novel();
        novel.setId(1);
        novel.setTitle("Test Novel");
        novel.setAuthorId(userId);
        novel.setStatus(1); // ACTIVE

        when(chapterRepository.findByUuid(chapterUuid)).thenReturn(existingChapter);
        when(novelService.getNovelEntity(existingChapter.getNovelId())).thenReturn(novel);
        when(chapterRepository.save(any(Chapter.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(redisUtil).deleteChapterCache(any(UUID.class));
        doNothing().when(redisUtil).deleteChapterCacheByNovelAndNumber(any(Integer.class), any(Integer.class));
        doNothing().when(redisUtil).invalidateChapterCaches(any(Integer.class));
        doNothing().when(kafkaEventProducerService).publishChapterUpdatedEvent(any(Chapter.class), any(Novel.class), any(UUID.class));

        // When
        ChapterDetailResponseDTO result = chapterService.updateChapter(userId, requestDTO);

        // Then
        assertNotNull(result);
        assertEquals("Updated Chapter", result.getTitle());
        assertEquals("Updated chapter content", result.getContent());
        assertEquals(150, result.getWordCnt());

        verify(chapterRepository, times(2)).findByUuid(chapterUuid); // Called once in updateChapter and once in getChapterByUuid
        verify(novelService).getNovelEntity(existingChapter.getNovelId());
        verify(chapterRepository).save(any(Chapter.class));
    }

    @Test
    void testUpdateChapter_NotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID chapterUuid = UUID.randomUUID();
        ChapterUpdateRequestDTO requestDTO = new ChapterUpdateRequestDTO();
        requestDTO.setUuid(chapterUuid);
        requestDTO.setTitle("Updated Chapter");

        when(chapterRepository.findByUuid(chapterUuid)).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            chapterService.updateChapter(userId, requestDTO);
        });

        verify(chapterRepository).findByUuid(chapterUuid);
    }

    @Test
    void testUpdateChapter_NotAuthor() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID differentUserId = UUID.randomUUID();
        UUID chapterUuid = UUID.randomUUID();
        ChapterUpdateRequestDTO requestDTO = new ChapterUpdateRequestDTO();
        requestDTO.setUuid(chapterUuid);
        requestDTO.setTitle("Updated Chapter");

        Chapter existingChapter = new Chapter();
        existingChapter.setId(1);
        existingChapter.setUuid(chapterUuid);
        existingChapter.setNovelId(1);
        existingChapter.setChapterNumber(1);
        existingChapter.setTitle("Original Chapter");
        existingChapter.setContent("Original content");
        existingChapter.setWordCnt(100);
        existingChapter.setIsValid(true);

        Novel novel = new Novel();
        novel.setId(1);
        novel.setTitle("Test Novel");
        novel.setAuthorId(differentUserId); // Different author
        novel.setStatus(1); // ACTIVE

        when(chapterRepository.findByUuid(chapterUuid)).thenReturn(existingChapter);
        when(novelService.getNovelEntity(existingChapter.getNovelId())).thenReturn(novel);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            chapterService.updateChapter(userId, requestDTO);
        });

        verify(chapterRepository).findByUuid(chapterUuid);
        verify(novelService).getNovelEntity(existingChapter.getNovelId());
    }

    @Test
    void testDeleteChapter_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID chapterUuid = UUID.randomUUID();

        Chapter existingChapter = new Chapter();
        existingChapter.setId(1);
        existingChapter.setUuid(chapterUuid);
        existingChapter.setNovelId(1);
        existingChapter.setChapterNumber(1);
        existingChapter.setTitle("Chapter to Delete");
        existingChapter.setContent("Content to delete");
        existingChapter.setWordCnt(100);
        existingChapter.setIsValid(true);

        Novel novel = new Novel();
        novel.setId(1);
        novel.setTitle("Test Novel");
        novel.setAuthorId(userId);
        novel.setStatus(1); // ACTIVE

        when(chapterRepository.findByUuid(chapterUuid)).thenReturn(existingChapter);
        when(novelService.getNovelEntity(existingChapter.getNovelId())).thenReturn(novel);
        doNothing().when(chapterRepository).softDeleteByUuid(chapterUuid);
        doNothing().when(redisUtil).deleteChapterCache(any(UUID.class));
        doNothing().when(redisUtil).deleteChapterCacheByNovelAndNumber(any(Integer.class), any(Integer.class));
        doNothing().when(redisUtil).invalidateChapterCaches(any(Integer.class));

        // When
        chapterService.deleteChapter(userId, chapterUuid);

        // Then
        verify(chapterRepository).findByUuid(chapterUuid);
        verify(novelService).getNovelEntity(existingChapter.getNovelId());
        verify(chapterRepository).softDeleteByUuid(chapterUuid);
    }

    @Test
    void testDeleteChapter_NotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID chapterUuid = UUID.randomUUID();

        when(chapterRepository.findByUuid(chapterUuid)).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            chapterService.deleteChapter(userId, chapterUuid);
        });

        verify(chapterRepository).findByUuid(chapterUuid);
    }

    @Test
    void testDeleteChapter_NotAuthor() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID differentUserId = UUID.randomUUID();
        UUID chapterUuid = UUID.randomUUID();

        Chapter existingChapter = new Chapter();
        existingChapter.setId(1);
        existingChapter.setUuid(chapterUuid);
        existingChapter.setNovelId(1);
        existingChapter.setChapterNumber(1);
        existingChapter.setTitle("Chapter to Delete");
        existingChapter.setContent("Content to delete");
        existingChapter.setWordCnt(100);
        existingChapter.setIsValid(true);

        Novel novel = new Novel();
        novel.setId(1);
        novel.setTitle("Test Novel");
        novel.setAuthorId(differentUserId); // Different author
        novel.setStatus(1); // ACTIVE

        when(chapterRepository.findByUuid(chapterUuid)).thenReturn(existingChapter);
        when(novelService.getNovelEntity(existingChapter.getNovelId())).thenReturn(novel);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            chapterService.deleteChapter(userId, chapterUuid);
        });

        verify(chapterRepository).findByUuid(chapterUuid);
        verify(novelService).getNovelEntity(existingChapter.getNovelId());
    }

    @Test
    void testGetChaptersByNovelId_Success() {
        // Given
        Integer novelId = 1;
        int page = 1;
        int pageSize = 5;
        boolean publishedOnly = true;

        Chapter chapter1 = new Chapter();
        chapter1.setId(1);
        chapter1.setUuid(UUID.randomUUID());
        chapter1.setNovelId(1);
        chapter1.setChapterNumber(1);
        chapter1.setTitle("Chapter 1");
        chapter1.setContent("Content 1");
        chapter1.setWordCnt(10);
        chapter1.setIsPremium(false);
        chapter1.setYuanCost(0.0f);
        chapter1.setViewCnt(100L);
        chapter1.setIsValid(true);
        chapter1.setCreateTime(new Date());
        chapter1.setUpdateTime(new Date());
        chapter1.setPublishTime(new Date());

        Chapter chapter2 = new Chapter();
        chapter2.setId(2);
        chapter2.setUuid(UUID.randomUUID());
        chapter2.setNovelId(1);
        chapter2.setChapterNumber(2);
        chapter2.setTitle("Chapter 2");
        chapter2.setContent("Content 2");
        chapter2.setWordCnt(15);
        chapter2.setIsPremium(false);
        chapter2.setYuanCost(0.0f);
        chapter2.setViewCnt(200L);
        chapter2.setIsValid(true);
        chapter2.setCreateTime(new Date());
        chapter2.setUpdateTime(new Date());
        chapter2.setPublishTime(new Date());

        List<Chapter> chapters = Arrays.asList(chapter1, chapter2);

        Novel novel = new Novel();
        novel.setId(1);
        novel.setTitle("Test Novel");
        novel.setStatus(1); // ACTIVE

        when(novelService.getNovelEntity(novelId)).thenReturn(novel);
        when(redisUtil.getCachedChapterListTyped(eq(novelId), any(String.class))).thenReturn(null);
        when(chapterRepository.findPublishedByNovelIdWithPagination(novelId, 0, pageSize)).thenReturn(chapters);
        when(chapterRepository.countPublishedByNovelId(novelId)).thenReturn(2L);
        doNothing().when(redisUtil).cacheChapterList(any(Integer.class), any(String.class), any(PageResponseDTO.class));

        // When
        PageResponseDTO<ChapterSummaryDTO> result = chapterService.getChaptersByNovelId(novelId, page, pageSize, publishedOnly);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2L, result.getTotalElements());
        assertEquals(0, result.getCurrentPage());
        assertEquals(pageSize, result.getSize());

        verify(novelService).getNovelEntity(novelId);
        verify(chapterRepository).findPublishedByNovelIdWithPagination(novelId, 0, pageSize);
        verify(chapterRepository).countPublishedByNovelId(novelId);
    }

    @Test
    void testGetNextChapterUuid_Success() {
        // Given
        UUID currentChapterUuid = UUID.randomUUID();
        UUID nextChapterUuid = UUID.randomUUID();

        Chapter currentChapter = new Chapter();
        currentChapter.setId(1);
        currentChapter.setUuid(currentChapterUuid);
        currentChapter.setNovelId(1);
        currentChapter.setChapterNumber(1);

        Chapter nextChapter = new Chapter();
        nextChapter.setId(2);
        nextChapter.setUuid(nextChapterUuid);
        nextChapter.setNovelId(1);
        nextChapter.setChapterNumber(2);

        when(chapterRepository.findByUuid(currentChapterUuid)).thenReturn(currentChapter);
        when(chapterRepository.findNextChapter(1, 1)).thenReturn(nextChapter);

        // When
        UUID result = chapterService.getNextChapterUuid(currentChapterUuid);

        // Then
        assertNotNull(result);
        assertEquals(nextChapterUuid, result);

        verify(chapterRepository).findByUuid(currentChapterUuid);
        verify(chapterRepository).findNextChapter(1, 1);
    }

    @Test
    void testGetNextChapterUuid_NotFound() {
        // Given
        UUID currentChapterUuid = UUID.randomUUID();

        when(chapterRepository.findByUuid(currentChapterUuid)).thenReturn(null);

        // When
        UUID result = chapterService.getNextChapterUuid(currentChapterUuid);

        // Then
        assertNull(result);

        verify(chapterRepository).findByUuid(currentChapterUuid);
    }

    @Test
    void testGetPreviousChapterUuid_Success() {
        // Given
        UUID currentChapterUuid = UUID.randomUUID();
        UUID previousChapterUuid = UUID.randomUUID();

        Chapter currentChapter = new Chapter();
        currentChapter.setId(2);
        currentChapter.setUuid(currentChapterUuid);
        currentChapter.setNovelId(1);
        currentChapter.setChapterNumber(2);

        Chapter previousChapter = new Chapter();
        previousChapter.setId(1);
        previousChapter.setUuid(previousChapterUuid);
        previousChapter.setNovelId(1);
        previousChapter.setChapterNumber(1);

        when(chapterRepository.findByUuid(currentChapterUuid)).thenReturn(currentChapter);
        when(chapterRepository.findPreviousChapter(1, 2)).thenReturn(previousChapter);

        // When
        UUID result = chapterService.getPreviousChapterUuid(currentChapterUuid);

        // Then
        assertNotNull(result);
        assertEquals(previousChapterUuid, result);

        verify(chapterRepository).findByUuid(currentChapterUuid);
        verify(chapterRepository).findPreviousChapter(1, 2);
    }

    @Test
    void testGetPreviousChapterUuid_NotFound() {
        // Given
        UUID currentChapterUuid = UUID.randomUUID();

        when(chapterRepository.findByUuid(currentChapterUuid)).thenReturn(null);

        // When
        UUID result = chapterService.getPreviousChapterUuid(currentChapterUuid);

        // Then
        assertNull(result);

        verify(chapterRepository).findByUuid(currentChapterUuid);
    }

    @Test
    void testGetChapterByUuid_WithNullUuid() {
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            chapterService.getChapterByUuid(null);
        });
    }
}