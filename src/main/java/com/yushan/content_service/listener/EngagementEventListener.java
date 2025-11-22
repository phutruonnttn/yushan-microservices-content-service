package com.yushan.content_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.content_service.service.IdempotencyService;
import com.yushan.content_service.service.NovelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for engagement service events
 * Handles events from engagement-service like novel rating updates
 */
@Slf4j
@Component
public class EngagementEventListener {

    @Autowired
    private NovelService novelService;

    @Autowired
    private IdempotencyService idempotencyService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String IDEMPOTENCY_PREFIX_RATING = "idempotency:novel-rating:";
    private static final String IDEMPOTENCY_PREFIX_VOTE_COUNT = "idempotency:novel-vote-count:";

    /**
     * Consume NovelRatingUpdateEvent from engagement service
     * Updates novel's average rating and review count
     */
    @KafkaListener(topics = "novel-rating-events", groupId = "content-service")
    public void handleNovelRatingUpdateEvent(@Payload String eventJson) {
        try {
            // Parse JSON to extract event data
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(eventJson);
            Integer novelId = jsonNode.get("novelId").asInt();
            Float avgRating = (float) jsonNode.get("avgRating").asDouble();
            Integer reviewCount = jsonNode.get("reviewCount").asInt();
            String idempotencyKey = jsonNode.has("idempotencyKey") 
                    ? jsonNode.get("idempotencyKey").asText() 
                    : novelId + "-" + System.currentTimeMillis();

            // Idempotency check: hybrid Redis + Database
            String idempotencyRedisKey = IDEMPOTENCY_PREFIX_RATING + idempotencyKey;
            if (idempotencyService.isProcessed(idempotencyRedisKey, "NovelRatingUpdate")) {
                log.info("Event already processed, skipping: novelId={}, idempotencyKey={}", 
                        novelId, idempotencyKey);
                return;
            }

            // Update novel rating and review count
            novelService.updateNovelRatingAndCount(novelId, avgRating, reviewCount);

            // Mark as processed (both Redis and Database)
            idempotencyService.markAsProcessed(idempotencyRedisKey, "NovelRatingUpdate");

            log.info("Successfully processed NOVEL_RATING_UPDATE event: novelId={}, avgRating={}, reviewCount={}", 
                    novelId, avgRating, reviewCount);

        } catch (Exception e) {
            log.error("Failed to process NOVEL_RATING_UPDATE event: {}", eventJson, e);
            // Exception will be handled by DefaultErrorHandler (retry 3 times)
            throw new RuntimeException("Failed to process novel rating update event", e);
        }
    }

    /**
     * Consume NovelVoteCountUpdateEvent from engagement service
     * Updates novel's vote count
     */
    @KafkaListener(topics = "novel-vote-count-events", groupId = "content-service")
    public void handleNovelVoteCountUpdateEvent(@Payload String eventJson) {
        try {
            // Parse JSON to extract event data
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(eventJson);
            Integer novelId = jsonNode.get("novelId").asInt();
            Integer voteCount = jsonNode.get("voteCount").asInt();
            String idempotencyKey = jsonNode.has("idempotencyKey") 
                    ? jsonNode.get("idempotencyKey").asText() 
                    : novelId + "-" + System.currentTimeMillis();

            // Idempotency check: hybrid Redis + Database
            String idempotencyRedisKey = IDEMPOTENCY_PREFIX_VOTE_COUNT + idempotencyKey;
            if (idempotencyService.isProcessed(idempotencyRedisKey, "NovelVoteCountUpdate")) {
                log.info("Event already processed, skipping: novelId={}, idempotencyKey={}", 
                        novelId, idempotencyKey);
                return;
            }

            // Update novel vote count
            novelService.updateNovelVoteCount(novelId, voteCount);

            // Mark as processed (both Redis and Database)
            idempotencyService.markAsProcessed(idempotencyRedisKey, "NovelVoteCountUpdate");

            log.info("Successfully processed NOVEL_VOTE_COUNT_UPDATE event: novelId={}, voteCount={}", 
                    novelId, voteCount);

        } catch (Exception e) {
            log.error("Failed to process NOVEL_VOTE_COUNT_UPDATE event: {}", eventJson, e);
            // Exception will be handled by DefaultErrorHandler (retry 3 times)
            throw new RuntimeException("Failed to process novel vote count update event", e);
        }
    }
}

