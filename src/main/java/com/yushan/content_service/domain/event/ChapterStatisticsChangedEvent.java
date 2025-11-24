package com.yushan.content_service.domain.event;

/**
 * Domain event representing a change in chapter data that requires
 * novel statistics (chapter count, word count) to be recalculated.
 */
public class ChapterStatisticsChangedEvent {

    private final Integer novelId;

    public ChapterStatisticsChangedEvent(Integer novelId) {
        this.novelId = novelId;
    }

    public Integer getNovelId() {
        return novelId;
    }

    @Override
    public String toString() {
        return "ChapterStatisticsChangedEvent{novelId=" + novelId + '}';
    }
}

