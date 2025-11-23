package com.yushan.content_service.repository.impl;

import com.yushan.content_service.dao.CategoryMapper;
import com.yushan.content_service.entity.Category;
import com.yushan.content_service.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyBatisCategoryRepository implements CategoryRepository {

    private final CategoryMapper categoryMapper;

    @Autowired
    public MyBatisCategoryRepository(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Category findById(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            categoryMapper.insertSelective(category);
        } else {
            categoryMapper.updateByPrimaryKeySelective(category);
        }
        return category;
    }

    @Override
    public void delete(Integer id) {
        categoryMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Category> findAll() {
        return categoryMapper.selectAll();
    }

    @Override
    public List<Category> findActiveCategories() {
        return categoryMapper.selectActiveCategories();
    }

    @Override
    public Category findBySlug(String slug) {
        return categoryMapper.selectBySlug(slug);
    }

    @Override
    public List<Category> findByIds(List<Integer> ids) {
        return categoryMapper.selectByIds(ids);
    }

    @Override
    public Category findByName(String name) {
        return categoryMapper.selectByName(name);
    }

    @Override
    public Category findByNameExcludingId(String name, Integer excludeId) {
        return categoryMapper.selectByNameExcludingId(name, excludeId);
    }

    @Override
    public Category findBySlugExcludingId(String slug, Integer excludeId) {
        return categoryMapper.selectBySlugExcludingId(slug, excludeId);
    }

    @Override
    public long countNovelsByCategory(Integer categoryId) {
        return categoryMapper.countNovelsByCategory(categoryId);
    }

    @Override
    public long countActiveNovelsByCategory(Integer categoryId) {
        return categoryMapper.countActiveNovelsByCategory(categoryId);
    }
}

