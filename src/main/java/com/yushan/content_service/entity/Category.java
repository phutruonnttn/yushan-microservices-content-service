package com.yushan.content_service.entity;

import java.util.Date;

/**
 * Category entity representing a novel category in the content service.
 * This entity maps to the 'category' table in the database.
 */
public class Category {
    private Integer id;
    private String name;
    private String description;
    private String slug;
    private Boolean isActive;
    private Date createTime;
    private Date updateTime;

    // Constructors
    public Category() {
        super();
    }

    public Category(Integer id, String name, String description, String slug, Boolean isActive, 
                   Date createTime, Date updateTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.isActive = isActive;
        this.createTime = createTime != null ? new Date(createTime.getTime()) : null;
        this.updateTime = updateTime != null ? new Date(updateTime.getTime()) : null;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug == null ? null : slug.trim();
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    // Business Logic Methods - Rich Domain Model

    /**
     * Activate the category.
     * Updates updateTime automatically.
     */
    public void activate() {
        this.isActive = true;
        updateTimestamp();
    }

    /**
     * Deactivate the category (soft delete).
     * Updates updateTime automatically.
     */
    public void deactivate() {
        this.isActive = false;
        updateTimestamp();
    }

    /**
     * Set active status.
     * Updates updateTime automatically.
     */
    public void setActiveStatus(boolean isActive) {
        this.isActive = isActive;
        updateTimestamp();
    }

    /**
     * Initialize a new category with default values.
     * This is called when creating a new category.
     */
    public void initializeAsNew() {
        Date now = new Date();
        this.createTime = now;
        this.updateTime = now;
        this.isActive = true;
    }

    /**
     * Update the update timestamp to current time.
     */
    public void updateTimestamp() {
        this.updateTime = new Date();
    }

    /**
     * Check if category is active.
     */
    public boolean isActiveCategory() {
        return Boolean.TRUE.equals(this.isActive);
    }

    /**
     * Check if category is inactive.
     */
    public boolean isInactive() {
        return !isActiveCategory();
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", slug='" + slug + '\'' +
                ", isActive=" + isActive +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
