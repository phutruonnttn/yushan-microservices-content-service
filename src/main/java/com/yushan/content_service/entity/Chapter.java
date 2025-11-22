package com.yushan.content_service.entity;

import java.util.Date;
import java.util.UUID;

/**
 * Chapter entity representing a chapter in a novel.
 * This entity maps to the 'chapter' table in the database.
 */
public class Chapter {
    private Integer id;
    private UUID uuid;
    private Integer novelId;
    private Integer chapterNumber;
    private String title;
    private String content;
    private Integer wordCnt;
    private Boolean isPremium;
    private Float yuanCost;
    private Long viewCnt;
    private Boolean isValid;
    private Date createTime;
    private Date updateTime;
    private Date publishTime;

    // Constructors
    public Chapter() {
        super();
    }

    public Chapter(Integer id, UUID uuid, Integer novelId, Integer chapterNumber, String title, 
                  String content, Integer wordCnt, Boolean isPremium, Float yuanCost, 
                  Long viewCnt, Boolean isValid, Date createTime, Date updateTime, Date publishTime) {
        this.id = id;
        this.uuid = uuid;
        this.novelId = novelId;
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.content = content;
        this.wordCnt = wordCnt;
        this.isPremium = isPremium;
        this.yuanCost = yuanCost;
        this.viewCnt = viewCnt;
        this.isValid = isValid;
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

    public Integer getNovelId() {
        return novelId;
    }

    public void setNovelId(Integer novelId) {
        this.novelId = novelId;
    }

    public Integer getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(Integer chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getWordCnt() {
        return wordCnt;
    }

    public void setWordCnt(Integer wordCnt) {
        this.wordCnt = wordCnt;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }

    public Float getYuanCost() {
        return yuanCost;
    }

    public void setYuanCost(Float yuanCost) {
        this.yuanCost = yuanCost;
    }

    public Long getViewCnt() {
        return viewCnt;
    }

    public void setViewCnt(Long viewCnt) {
        this.viewCnt = viewCnt;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
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
     * Mark chapter as valid (published).
     * Updates updateTime automatically.
     */
    public void markAsValid() {
        this.isValid = true;
        updateTimestamp();
    }

    /**
     * Mark chapter as invalid (unpublished/soft deleted).
     * Updates updateTime automatically.
     */
    public void markAsInvalid() {
        this.isValid = false;
        updateTimestamp();
    }

    /**
     * Set validity status.
     * Updates updateTime automatically.
     */
    public void setValidityStatus(boolean isValid) {
        this.isValid = isValid;
        updateTimestamp();
    }

    /**
     * Mark chapter as premium.
     * Updates updateTime automatically.
     */
    public void markAsPremium() {
        this.isPremium = true;
        updateTimestamp();
    }

    /**
     * Mark chapter as free (not premium).
     * Updates updateTime automatically.
     */
    public void markAsFree() {
        this.isPremium = false;
        updateTimestamp();
    }

    /**
     * Set premium status.
     * Updates updateTime automatically.
     */
    public void setPremiumStatus(boolean isPremium) {
        this.isPremium = isPremium;
        updateTimestamp();
    }

    /**
     * Publish the chapter.
     * Sets isValid to true and publish time if not already set.
     */
    public void publish() {
        this.isValid = true;
        if (this.publishTime == null) {
            this.publishTime = new Date();
        }
        updateTimestamp();
    }

    /**
     * Publish the chapter with a specific publish time.
     */
    public void publish(Date publishTime) {
        this.isValid = true;
        this.publishTime = publishTime != null ? new Date(publishTime.getTime()) : new Date();
        updateTimestamp();
    }

    /**
     * Unpublish the chapter (soft delete).
     */
    public void unpublish() {
        this.isValid = false;
        updateTimestamp();
    }

    /**
     * Update word count and recalculate if content is provided.
     * Updates updateTime automatically.
     */
    public void updateWordCount(Integer wordCount) {
        this.wordCnt = wordCount;
        updateTimestamp();
    }

    /**
     * Update word count based on content length.
     * Updates updateTime automatically.
     */
    public void recalculateWordCount() {
        if (this.content != null && !this.content.trim().isEmpty()) {
            this.wordCnt = this.content.trim().length();
            updateTimestamp();
        }
    }

    /**
     * Update content and recalculate word count.
     * Updates updateTime automatically.
     */
    public void updateContent(String content) {
        this.content = content != null ? content.trim() : null;
        recalculateWordCount();
    }

    /**
     * Set price for premium chapter.
     * Updates updateTime automatically.
     */
    public void setPrice(float yuanCost) {
        if (yuanCost < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.yuanCost = yuanCost;
        updateTimestamp();
    }

    /**
     * Initialize a new chapter with default values.
     * This is called when creating a new chapter.
     */
    public void initializeAsNew() {
        Date now = new Date();
        this.createTime = now;
        this.updateTime = now;
        this.viewCnt = 0L;
        this.isValid = true;
        if (this.publishTime == null) {
            this.publishTime = now;
        }
    }

    /**
     * Update the update timestamp to current time.
     */
    public void updateTimestamp() {
        this.updateTime = new Date();
    }

    /**
     * Check if chapter is valid (published).
     */
    public boolean isValidChapter() {
        return Boolean.TRUE.equals(this.isValid);
    }

    /**
     * Check if chapter is premium.
     */
    public boolean isPremiumChapter() {
        return Boolean.TRUE.equals(this.isPremium);
    }

    /**
     * Check if chapter is free.
     */
    public boolean isFreeChapter() {
        return !isPremiumChapter();
    }

    /**
     * Check if chapter is published (valid and publish time has passed).
     */
    public boolean isPublished() {
        if (!isValidChapter()) {
            return false;
        }
        Date now = new Date();
        return this.publishTime != null && !this.publishTime.after(now);
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", novelId=" + novelId +
                ", chapterNumber=" + chapterNumber +
                ", title='" + title + '\'' +
                ", wordCnt=" + wordCnt +
                ", isPremium=" + isPremium +
                ", yuanCost=" + yuanCost +
                ", viewCnt=" + viewCnt +
                ", isValid=" + isValid +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", publishTime=" + publishTime +
                '}';
    }
}
