package com.exadel.sandbox.service.impl;

import com.exadel.sandbox.dto.pagelist.PageList;
import com.exadel.sandbox.dto.request.category.CategoryRequest;
import com.exadel.sandbox.dto.response.category.CategoryResponse;
import com.exadel.sandbox.dto.response.category.CategoryShortResponse;
import com.exadel.sandbox.dto.response.filter.CategoryFilterResponse;
import com.exadel.sandbox.mappers.category.CategoryMapper;
import com.exadel.sandbox.mappers.category.CategoryShortMapper;
import com.exadel.sandbox.model.vendorinfo.Category;
import com.exadel.sandbox.model.vendorinfo.Event;
import com.exadel.sandbox.repository.category.CategoryRepository;
import com.exadel.sandbox.service.CategoryService;
import com.exadel.sandbox.service.EventService;
import com.exadel.sandbox.service.TagService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_FIELD_SORT = "name";

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryShortMapper categoryShortMapper;
    private final EventService eventService;
    private final TagService tagService;

    @Override
    @Transactional
    public void deleteCategoryById(Long categoryId) {
        log.debug(">>>>>>>>delete category id " + categoryId);

        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            Set<Event> events = categoryOptional.get().getEvents();
            if (events.isEmpty() || events.size() == 0) {
                categoryRepository.deleteById(categoryId);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                        "You cannot delete category. Category is uses");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "Category is not exists");
        }
    }

    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        log.debug(">>>>>update category with id: " + categoryId);

        if (categoryRequest.getName() == null || categoryRequest.getName().equals("")) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE
                    , "Category name cannot be empty");
        }

        if (categoryId != categoryRequest.getId()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE
                    , "Category name cannot be empty");
        }

        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if (categoryOptional.isPresent()) {
            Category category = categoryMapper.categoryRequestToCategory(categoryRequest);
            category.getTags().forEach(tag -> tag.setCategory(category));
            Category savedCategory = categoryRepository.save(category);

            return categoryMapper.categoryToCategoryResponse(savedCategory);

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found. ProductId: "
                    + categoryId);
        }
    }

    @Override
    public PageList<CategoryResponse> listCategoriesByPartOfName(String categoryName, Integer pageNumber, Integer pageSize) {
        log.debug(">>>>>>>>>>>>>ListCategoryByPartOfName ...." + categoryName);

        categoryName = categoryName.isEmpty() ? "" : categoryName;

        pageNumber = getPageNumber(pageNumber);
        pageSize = getPageSize(pageSize);

        Page<Category> categoryPage = categoryRepository.findAllByNameContainingIgnoreCaseOrderByNameAsc(categoryName,
                PageRequest.of(pageNumber, pageSize, Sort.by(DEFAULT_FIELD_SORT).ascending()));

        return new PageList<CategoryResponse>(categoryPage.stream()
                .map(categoryMapper::categoryToCategoryResponse)
                .collect(Collectors.toList()), categoryPage);
    }

    @Override
    public CategoryResponse findCategoryById(Long categoryId) {
        log.debug(">>>>>>CategoryService find by Id: " + categoryId);

        Optional<Category> category = categoryRepository.findById(categoryId);

        if (category.isPresent()) {
            log.debug(">>>>>Category is found: " + categoryId);
            return categoryMapper.categoryToCategoryResponse(category.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found Category");
        }
    }

    @Override
    public PageList<CategoryResponse> listCategories(Integer pageNumber, Integer pageSize) {
        log.debug(">>>>>>>>>>>>>ListCategory ....");

        pageNumber = getPageNumber(pageNumber);
        pageSize = getPageSize(pageSize);

        Page<Category> categoryPage = categoryRepository.findAllByOrderByNameAsc(
                PageRequest.of(pageNumber, pageSize, Sort.by(DEFAULT_FIELD_SORT).ascending()));

        return new PageList<CategoryResponse>(categoryPage.getContent().stream()
                .map(categoryMapper::categoryToCategoryResponse)
                .collect(Collectors.toList()), categoryPage);

    }

    @Override
    @Transactional
    public CategoryResponse saveCategory(CategoryRequest categoryRequest) {
        String categoryName = categoryRequest.getName();

        if (categoryName == null || categoryName.equals("")) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "The category cannot be empty");
        }

        if (isCategoryNameExists(categoryName)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The category: "
                    + categoryName + " is already exists");
        }

        var category = categoryMapper.categoryRequestToCategory(categoryRequest);

        category.getTags().forEach(tag -> tag.setCategory(category));

        Category savedCategory = categoryRepository.saveAndFlush(category);
        return categoryMapper.categoryToCategoryResponse(savedCategory);
    }

    @Override
    public boolean isCategoryNameExists(String categoryName) {
        return categoryRepository.findByName(categoryName) != null;
    }

    @Override
    public List<CategoryShortResponse> findAllCategoriesByVendorId(Long vendorId) {
        return categoryRepository.findDistinctByEventsVendorIdOrderByNameAsc(vendorId).stream().distinct()
                .map(categoryShortMapper::categoryToCategoryShortResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryShortResponse> findAllByVendorId(Long vendorId) {
        return categoryRepository.findAllByVendorId(vendorId).stream()
                .map(categoryShortMapper::categoryToCategoryShortResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryFilterResponse> findAllCategoryByLocationFilter(Long id, boolean isCountry) {
        return categoryRepository.findAllByLocationFilterId(id, isCountry).stream()
                .map(categoryMapper::categoryToCategoryFilterResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryFilterResponse> findAllCategoryByLocationFilterFavorites(Long userId, Long id, boolean isCountry) {
        return categoryRepository.findAllByLocationFilterIdFavorites(userId, id, isCountry).stream()
                .map(categoryMapper::categoryToCategoryFilterResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryFilterResponse> findAllCategoryByVendorFilter(List<Long> ids) {
        return categoryRepository.findAllByVendorFilterIds(ids).stream()
                .map(categoryMapper::categoryToCategoryFilterResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryFilterResponse> findAllCategoryFilter() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::categoryToCategoryFilterResponse)
                .collect(Collectors.toList());
    }

    private int getPageNumber(Integer pageNumber) {
        return pageNumber == null || pageNumber < 0 ? DEFAULT_PAGE_NUMBER : pageNumber;
    }

    private int getPageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
    }
}