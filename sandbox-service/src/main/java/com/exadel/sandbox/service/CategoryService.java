package com.exadel.sandbox.service;

import com.exadel.sandbox.dto.pagelist.PageList;
import com.exadel.sandbox.dto.request.category.CategoryRequest;
import com.exadel.sandbox.dto.response.category.CategoryResponse;
import com.exadel.sandbox.dto.response.category.CategoryShortResponse;
import com.exadel.sandbox.dto.response.filter.CategoryFilterResponse;

import java.util.List;
import java.util.Set;

public interface CategoryService {

    void deleteCategoryById(Long categoryId);

    CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest);

    PageList<CategoryResponse> listCategoriesByPartOfName(String categoryName, Integer pageNumber, Integer pageSize);

    CategoryResponse findCategoryById(Long categoryId);

    CategoryResponse saveCategory(CategoryRequest categoryRequest);

    boolean isCategoryNameExists(String categoryName);

    PageList<CategoryResponse> listCategories(Integer pageNumber, Integer pageSize);

    List<CategoryShortResponse> findAllCategoriesByVendorId(Long vendorId);

    List<CategoryShortResponse>findAllByVendorId(Long vendorId);

    List<CategoryFilterResponse>findAllCategoryByLocationFilter(Long id, boolean isCountry);

    List<CategoryFilterResponse>findAllCategoryByLocationFilterFavorites(Long userId, Long id, boolean isCountry);

    List<CategoryFilterResponse>findAllCategoryByVendorFilter(List<Long> ids);

    List<CategoryFilterResponse>findAllCategoryFilter();

}
