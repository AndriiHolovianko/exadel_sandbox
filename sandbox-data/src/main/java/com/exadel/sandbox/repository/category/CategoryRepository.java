package com.exadel.sandbox.repository.category;

import com.exadel.sandbox.model.vendorinfo.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    Category findByName(String categoryName);

    Page<Category> findAllByNameContainingIgnoreCaseOrderByNameAsc(String categoryName, Pageable pageable);

    Page<Category> findAllByOrderByNameAsc(Pageable pageable);

    List<Category> findDistinctByEventsVendorIdOrderByNameAsc(Long vendorId);

    List<Category> findAllByVendorFilterIds(List<Long> ids);

    @Query("select distinct c.id from Category c " +
            "join c.tags tag " +
            "where tag.id in (?1)")
    Set<Long> findCategoryIdByTagsId(Set<Long> tagsId);

    @Query(value = "SELECT c.* FROM category c " +
            "JOIN event e on e.category_id=c.id " +
            "JOIN saved_event se on se.event_id=e.id " +
            "WHERE se.user_id = ?1", nativeQuery = true)
    Set<Category> getAllCategoriesFromSaved(Long userId);
}