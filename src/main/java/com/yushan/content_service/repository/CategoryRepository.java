package com.yushan.content_service.repository;

import com.yushan.content_service.entity.Category;
import java.util.List;

/**
 * Repository interface for Category aggregate.
 * Abstracts data access operations for Category entity.
 */
public interface CategoryRepository {
    
    // Basic CRUD operations
    Category findById(Integer id);
    
    Category save(Category category);
    
    void delete(Integer id);
    
    // Query operations
    List<Category> findAll();
    
    List<Category> findActiveCategories();
    
    Category findBySlug(String slug);
    
    List<Category> findByIds(List<Integer> ids);
    
    // Validation methods
    Category findByName(String name);
    
    Category findByNameExcludingId(String name, Integer excludeId);
    
    Category findBySlugExcludingId(String slug, Integer excludeId);
    
    // Statistics methods
    long countNovelsByCategory(Integer categoryId);
    
    long countActiveNovelsByCategory(Integer categoryId);
}

