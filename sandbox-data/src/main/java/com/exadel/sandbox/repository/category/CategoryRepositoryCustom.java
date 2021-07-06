package com.exadel.sandbox.repository.category;

import com.exadel.sandbox.model.vendorinfo.Category;

import java.util.List;

public interface CategoryRepositoryCustom {

    List<Category> findAllByVendorId(Long vendorId);

    List<Category> findAllByLocationFilterId(Long id, boolean isCountry);

    List<Category> findAllByVendorFilterIds(List<Long> ids);
}
