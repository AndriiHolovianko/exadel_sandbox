package com.exadel.sandbox.service.filter.impl;

import com.exadel.sandbox.dto.request.filter.FilterRequest;
import com.exadel.sandbox.dto.response.city.CityResponse;
import com.exadel.sandbox.dto.response.filter.CategoryFilterResponse;
import com.exadel.sandbox.dto.response.filter.FilterResponse;
import com.exadel.sandbox.dto.response.filter.LocationFilterResponse;
import com.exadel.sandbox.dto.response.filter.TagFilterResponse;
import com.exadel.sandbox.dto.response.filter.VendorFilterResponse;
import com.exadel.sandbox.mappers.category.CategoryMapper;
import com.exadel.sandbox.mappers.vendor.VendorMapper;
import com.exadel.sandbox.repository.category.CategoryRepository;
import com.exadel.sandbox.repository.vendor.VendorRepository;
import com.exadel.sandbox.service.CategoryService;
import com.exadel.sandbox.service.CityService;
import com.exadel.sandbox.service.LocationService;
import com.exadel.sandbox.service.TagService;
import com.exadel.sandbox.service.VendorDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class FilterServiceFoFavorites {

    private CategoryService categoryService;
    private VendorDetailsService vendorService;
    private LocationService locationService;
    private TagService tagService;
    private CityService cityService;
    private CategoryRepository categoryRepository;
    private CategoryMapper categoryMapper;
    private VendorRepository vendorRepository;
    private VendorMapper vendorMapper;


    public FilterResponse getFilterResponse(FilterRequest filterRequest, Long userId) {

        switch (filterRequest.getMain()) {
            case "location":
                return getFilterResponseMainLocation(filterRequest, userId);
            case "categories":
                return getFilterResponseMainCategories(filterRequest);
            default:
                return getFilterResponseAll(userId);
        }
    }

    private FilterResponse getFilterResponseAll(Long userId) {

        CityResponse cityByUserId = cityService.findCityByUserId(userId);

        List<LocationFilterResponse> allLocationFilter = locationService.findAllLocationFilterFavorites(userId);
        allLocationFilter.stream()
                .filter(locationFilterResponse -> locationFilterResponse.isCountry() == false &&
                        locationFilterResponse.getId() == cityByUserId.getId())
                .forEach(locationFilterResponse -> locationFilterResponse.setChecked(true));

        List<CategoryFilterResponse> allCategoriesFilter = categoryRepository.getAllCategoriesFromSaved(userId).stream()
                .map(categoryMapper::categoryToCategoryFilterResponse)
                .collect(Collectors.toList());

        List<TagFilterResponse> allTagsFilter = Collections.emptyList();

        List<VendorFilterResponse> allVendorsFilter = vendorRepository.getAllVendorsFromSaved(userId).stream()
                .map(vendorMapper::vendorToVendorFilterResponse)
                .collect(Collectors.toList());

        return new FilterResponse(allLocationFilter, allCategoriesFilter, allTagsFilter, allVendorsFilter);
    }

    private FilterResponse getFilterResponseMainLocation(FilterRequest filterRequest, Long userId) {

        List<CategoryFilterResponse> allCategoriesByLocationFilter =
                getAllCategiriesByLocationFilterFavorites(userId, filterRequest.getLocationId(), filterRequest.getIsCountry());

        List<TagFilterResponse> allTagsByCategoryFilter = Collections.emptyList();

        List<VendorFilterResponse> allVendorsByLocationFilter =
                getAllVendorsByLocationFilterFavorites(userId, filterRequest.getLocationId(), filterRequest.getIsCountry());

        return new FilterResponse(null, allCategoriesByLocationFilter, allTagsByCategoryFilter, allVendorsByLocationFilter);
    }

    private FilterResponse getFilterResponseMainCategories(FilterRequest filterRequest) {

        List<TagFilterResponse> allTagsByCategoryFilter =
                getAllTagsByCategoryFilter(filterRequest.getCategories());

        return new FilterResponse(null, null, allTagsByCategoryFilter, null);
    }

    private List<TagFilterResponse> getAllTagsByCategoryFilter(List<Long> ids) {

        if (ids.isEmpty() || ids.size() == 0) {
            return Collections.emptyList();
        } else {
            return tagService.findAllTagsByCategoryFilter(ids);
        }
    }


    private List<CategoryFilterResponse> getAllCategiriesByLocationFilterFavorites(long userId, long locationId, boolean isCountry) {
        return categoryService.findAllCategoryByLocationFilterFavorites(userId, locationId, isCountry);
    }

    private List<VendorFilterResponse> getAllVendorsByLocationFilterFavorites(long userId, long locationId, boolean isCountry) {
        return vendorService.findAllVendorByLocationFilterFavorites(userId, locationId, isCountry);
    }
}
