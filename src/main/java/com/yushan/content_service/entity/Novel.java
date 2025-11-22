package com.yushan.content_service.entity;

import com.yushan.content_service.enums.NovelStatus;
import java.util.Date;
import java.util.UUID;

/**
 * Novel entity representing a web novel in the content service.
 * This entity maps to the 'novel' table in the database.
 */
public class Novel {
    private Integer id;
    private UUID uuid;
    private String title;
    private UUID authorId;
    private String authorName;
    private Integer categoryId;
    private String synopsis;
    private String coverImgUrl;
    private Integer status;
    private Boolean isCompleted;
    private Integer chapterCnt;
    private Long wordCnt;
    private Float avgRating;
    private Integer reviewCnt;
    private Long viewCnt;
    private Integer voteCnt;
    private Float yuanCnt;
    private Date createTime;
    private Date updateTime;
    private Date publishTime;

    // Constructors
    public Novel() {
        super();
    }

    public Novel(Integer id, UUID uuid, String title, UUID authorId, String authorName, 
                Integer categoryId, String synopsis, String coverImgUrl, Integer status, 
                Boolean isCompleted, Integer chapterCnt, Long wordCnt, Float avgRating, 
                Integer reviewCnt, Long viewCnt, Integer voteCnt, Float yuanCnt, 
                Date createTime, Date updateTime, Date publishTime) {
        this.id = id;
        this.uuid = uuid;
        this.title = title;
        this.authorId = authorId;
        this.authorName = authorName;
        this.categoryId = categoryId;
        this.synopsis = synopsis;
        this.coverImgUrl = coverImgUrl;
        this.status = status;
        this.isCompleted = isCompleted;
        this.chapterCnt = chapterCnt;
        this.wordCnt = wordCnt;
        this.avgRating = avgRating;
        this.reviewCnt = reviewCnt;
        this.viewCnt = viewCnt;
        this.voteCnt = voteCnt;
        this.yuanCnt = yuanCnt;
        this.createTime = createTime != null ? new Date(createTime.getTime()) : null;
        this.updateTime = updateTime != null ? new Date(updateTime.getTime()) : null;
        this.publishTime = publishTime != null ? new Date(publishTime.getTime()) : null;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName == null ? null : authorName.trim();
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis == null ? null : synopsis.trim();
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl == null ? null : coverImgUrl.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Integer getChapterCnt() {
        return chapterCnt;
    }

    public void setChapterCnt(Integer chapterCnt) {
        this.chapterCnt = chapterCnt;
    }

    public Long getWordCnt() {
        return wordCnt;
    }

    public void setWordCnt(Long wordCnt) {
        this.wordCnt = wordCnt;
    }

    public Float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Float avgRating) {
        this.avgRating = avgRating;
    }

    public Integer getReviewCnt() {
        return reviewCnt;
    }

    public void setReviewCnt(Integer reviewCnt) {
        this.reviewCnt = reviewCnt;
    }

    public Long getViewCnt() {
        return viewCnt;
    }

    public void setViewCnt(Long viewCnt) {
        this.viewCnt = viewCnt;
    }

    public Integer getVoteCnt() {
        return voteCnt;
    }

    public void setVoteCnt(Integer voteCnt) {
        this.voteCnt = voteCnt;
    }

    public Float getYuanCnt() {
        return yuanCnt;
    }

    public void setYuanCnt(Float yuanCnt) {
        this.yuanCnt = yuanCnt;
    }

    public Date getCreateTime() {
        return createTime != null ? new Date(createTime.getTime()) : null;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime != null ? new Date(createTime.getTime()) : null;
    }

    public Date getUpdateTime() {
        return updateTime != null ? new Date(updateTime.getTime()) : null;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime != null ? new Date(updateTime.getTime()) : null;
    }

    public Date getPublishTime() {
        return publishTime != null ? new Date(publishTime.getTime()) : null;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime != null ? new Date(publishTime.getTime()) : null;
    }

    // Business Logic Methods - Rich Domain Model

    /**
     * Change novel status with business logic.
     * Updates updateTime automatically.
     */
    public void changeStatus(NovelStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = newStatus.getValue();
        updateTimestamp();
        
        // Set publish time if publishing
        if (newStatus == NovelStatus.PUBLISHED && this.publishTime == null) {
            this.publishTime = new Date();
        }
    }

    /**
     * Change status from current status to a new status with validation.
     * This method allows status transitions to be controlled.
     */
    public void changeStatusTo(NovelStatus newStatus, NovelStatus... allowedFromStatuses) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        if (allowedFromStatuses != null && allowedFromStatuses.length > 0) {
            NovelStatus currentStatus = NovelStatus.fromValue(this.status);
            boolean allowed = false;
            for (NovelStatus allowedStatus : allowedFromStatuses) {
                if (currentStatus == allowedStatus) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                throw new IllegalStateException(
                    String.format("Cannot change status from %s to %s", 
                        currentStatus.name(), newStatus.name()));
            }
        }
        
        changeStatus(newStatus);
    }

    /**
     * Mark novel as published.
     * Sets status to PUBLISHED and publish time if not already set.
     */
    public void publish() {
        changeStatus(NovelStatus.PUBLISHED);
        if (this.publishTime == null) {
            this.publishTime = new Date();
        }
    }

    /**
     * Mark novel as draft.
     */
    public void markAsDraft() {
        changeStatus(NovelStatus.DRAFT);
    }

    /**
     * Submit novel for review.
     */
    public void submitForReview() {
        changeStatusTo(NovelStatus.UNDER_REVIEW, NovelStatus.DRAFT);
    }

    /**
     * Archive the novel.
     */
    public void archive() {
        changeStatusTo(NovelStatus.ARCHIVED, NovelStatus.DRAFT, NovelStatus.PUBLISHED, NovelStatus.HIDDEN);
    }

    /**
     * Unarchive the novel (change from ARCHIVED to DRAFT).
     */
    public void unarchive() {
        changeStatusTo(NovelStatus.DRAFT, NovelStatus.ARCHIVED);
    }

    /**
     * Hide the novel (from PUBLISHED to HIDDEN).
     */
    public void hide() {
        changeStatusTo(NovelStatus.HIDDEN, NovelStatus.PUBLISHED);
    }

    /**
     * Unhide the novel (from HIDDEN back to PUBLISHED).
     */
    public void unhide() {
        changeStatusTo(NovelStatus.PUBLISHED, NovelStatus.HIDDEN);
    }

    /**
     * Change status to UNDER_REVIEW when editing published/hidden novel.
     * This is used when editing published or hidden novels.
     */
    public void markForReviewAfterEdit() {
        NovelStatus currentStatus = NovelStatus.fromValue(this.status);
        if (currentStatus == NovelStatus.PUBLISHED || currentStatus == NovelStatus.HIDDEN) {
            changeStatus(NovelStatus.UNDER_REVIEW);
        }
    }

    /**
     * Mark novel as completed.
     * Updates updateTime automatically.
     */
    public void markAsCompleted() {
        this.isCompleted = true;
        updateTimestamp();
    }

    /**
     * Mark novel as ongoing (not completed).
     * Updates updateTime automatically.
     */
    public void markAsOngoing() {
        this.isCompleted = false;
        updateTimestamp();
    }

    /**
     * Set completion status.
     * Updates updateTime automatically.
     */
    public void setCompletionStatus(boolean completed) {
        this.isCompleted = completed;
        updateTimestamp();
    }

    /**
     * Update novel statistics (chapter count and word count).
     * Updates updateTime automatically.
     */
    public void updateStatistics(int chapterCount, long wordCount) {
        this.chapterCnt = chapterCount;
        this.wordCnt = wordCount;
        updateTimestamp();
    }

    /**
     * Update novel's rating and review count.
     * Updates updateTime automatically.
     */
    public void updateRatingStatistics(float avgRating, int reviewCount) {
        this.avgRating = avgRating;
        this.reviewCnt = reviewCount;
        updateTimestamp();
    }

    /**
     * Initialize a new novel with default values.
     * This is called when creating a new novel.
     */
    public void initializeAsNew() {
        Date now = new Date();
        this.createTime = now;
        this.updateTime = now;
        this.status = NovelStatus.DRAFT.getValue();
        this.isCompleted = false;
        this.chapterCnt = 0;
        this.wordCnt = 0L;
        this.avgRating = 0.0f;
        this.reviewCnt = 0;
        this.viewCnt = 0L;
        this.voteCnt = 0;
        this.yuanCnt = 0.0f;
        this.publishTime = null;
    }

    /**
     * Update the update timestamp to current time.
     */
    public void updateTimestamp() {
        this.updateTime = new Date();
    }

    /**
     * Check if novel is in a specific status.
     */
    public boolean isStatus(NovelStatus status) {
        return status != null && this.status.equals(status.getValue());
    }

    /**
     * Check if novel can be edited.
     */
    public boolean canBeEdited() {
        NovelStatus currentStatus = NovelStatus.fromValue(this.status);
        return currentStatus == NovelStatus.DRAFT || 
               currentStatus == NovelStatus.PUBLISHED || 
               currentStatus == NovelStatus.HIDDEN ||
               currentStatus == NovelStatus.UNDER_REVIEW;
    }

    /**
     * Check if novel can be archived.
     */
    public boolean canBeArchived() {
        NovelStatus currentStatus = NovelStatus.fromValue(this.status);
        return currentStatus == NovelStatus.DRAFT || 
               currentStatus == NovelStatus.PUBLISHED || 
               currentStatus == NovelStatus.HIDDEN;
    }

    /**
     * Check if novel is published.
     */
    public boolean isPublished() {
        return isStatus(NovelStatus.PUBLISHED);
    }

    /**
     * Check if novel is draft.
     */
    public boolean isDraft() {
        return isStatus(NovelStatus.DRAFT);
    }

    /**
     * Check if novel is archived.
     */
    public boolean isArchived() {
        return isStatus(NovelStatus.ARCHIVED);
    }

    @Override
    public String toString() {
        return "Novel{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", title='" + title + '\'' +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", categoryId=" + categoryId +
                ", status=" + status +
                ", isCompleted=" + isCompleted +
                ", chapterCnt=" + chapterCnt +
                ", wordCnt=" + wordCnt +
                ", viewCnt=" + viewCnt +
                ", createTime=" + createTime +
                '}';
    }
}
