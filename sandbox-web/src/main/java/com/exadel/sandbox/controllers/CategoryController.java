package com.exadel.sandbox.controllers;

import com.exadel.sandbox.dto.pagelist.CategoryPagedList;
import com.exadel.sandbox.dto.request.category.CategoryRequest;
import com.exadel.sandbox.dto.response.category.CategoryResponse;
import com.exadel.sandbox.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @DeleteMapping(path = {"category/{categoryId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("categoryId") Long categoryId) {

        log.debug(">>>>>>>>>>controller delete category by Id");

        categoryService.deleteCategoryById(categoryId);
    }

    @PutMapping(path = {"category/{categoryId}"}, produces = {"application/json"})
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable("categoryId") Long categoryId,
                                                           @Valid @RequestBody CategoryRequest categoryRequest) {

        return new ResponseEntity<>(
                categoryService.updateCategory(categoryId, categoryRequest)
                , HttpStatus.OK);
    }

    @GetMapping(produces = {"application/json"}, path = "categoryname/{name}")
    public ResponseEntity<CategoryPagedList> getCategoriesByPartOfName(
            @PathVariable("name") String categoryName,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "0") Integer pageSize) {

        log.debug(">>>>>>>>>>Category List by part of name: " + categoryName);

        CategoryPagedList categoryList =
                categoryService.listCategoriesByPartOfName(categoryName, pageNumber, pageSize);

        return new ResponseEntity<>(categoryList, HttpStatus.OK);
    }

    @GetMapping(produces = {"application/json"}, path = "category/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable("categoryId") Long categoryId) {

        log.debug(">>>>>>getCategoryById " + categoryId);

        return new ResponseEntity<>(categoryService.findCategoryById(categoryId), HttpStatus.OK);
    }

    @GetMapping(produces = {"application/json"}, path = "category")
    public ResponseEntity<CategoryPagedList> listCategories(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "0") Integer pageSize) {

        log.debug(">>>>List all categories");

        return new ResponseEntity<>(categoryService.listCategories(pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping(produces = {"application/json"},
            consumes = {"application/json"},
            path = {"category", "category/"})
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return new ResponseEntity<>(categoryService.saveCategory(categoryRequest), HttpStatus.OK);
    }


}
