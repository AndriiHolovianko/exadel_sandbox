package com.exadel.sandbox.repository.vendor;

import com.exadel.sandbox.model.vendorinfo.Vendor;

import java.util.List;

public interface VendorRepositoryCustom {

    List<Vendor> findAllByUserLocation(String city);
    List<Vendor> findAllByLocationFilterId(Long id, boolean isCountry);
    List<Vendor> findAllByCategoryFilterIds(List<Long> ids);
    List<Vendor> findAllByLocationFilterIdFavorites(Long userId, Long id, boolean isCountry);
}
