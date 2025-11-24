package com.yushan.content_service.domain.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publishes internal domain events for the Chapter aggregate.
 */
@Component
public class ChapterDomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public ChapterDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Publish an event that chapter data changed and novel statistics must be recalculated.
     */
    public void publishChapterStatisticsChanged(Integer novelId) {
        if (novelId == null) {
            return;
        }
        applicationEventPublisher.publishEvent(new ChapterStatisticsChangedEvent(novelId));
    }
}

