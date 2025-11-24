package com.yushan.content_service.domain.event;

import com.yushan.content_service.repository.ChapterRepository;
import com.yushan.content_service.service.NovelService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles chapter-related domain events and coordinates aggregate updates.
 */
@Component
public class ChapterDomainEventListener {

    private final ChapterRepository chapterRepository;
    private final NovelService novelService;

    public ChapterDomainEventListener(ChapterRepository chapterRepository,
                                      NovelService novelService) {
        this.chapterRepository = chapterRepository;
        this.novelService = novelService;
    }

    /**
     * Recalculate novel statistics synchronously within the same transaction
     * when chapter data changes.
     */
    @EventListener
    @Transactional
    public void handleChapterStatisticsChanged(ChapterStatisticsChangedEvent event) {
        Integer novelId = event.getNovelId();
        long chapterCount = chapterRepository.countPublishedByNovelId(novelId);
        long wordCount = chapterRepository.sumPublishedWordCountByNovelId(novelId);
        novelService.updateNovelStatistics(novelId, (int) chapterCount, wordCount);
    }
}

