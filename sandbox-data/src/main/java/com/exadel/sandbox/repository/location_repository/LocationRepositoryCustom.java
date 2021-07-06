package com.exadel.sandbox.repository.location_repository;

import com.exadel.sandbox.model.LocationFilter;
import com.exadel.sandbox.model.location.Location;
import com.exadel.sandbox.model.vendorinfo.Vendor;

import java.util.List;

public interface LocationRepositoryCustom {

    List<LocationFilter> findAllByCategoryFilterIds(List<Long>ids);
    List<LocationFilter> findAllByVendorFilterIds(List<Long> ids);
    List<LocationFilter>getAllLocationFilter();
    List<LocationFilter> getAllEventsLocationsFromSaved(Long userId);
}
