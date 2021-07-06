package com.exadel.sandbox.service;

import com.exadel.sandbox.dto.request.vendor.VendorRequest;
import com.exadel.sandbox.dto.request.vendor.VendorUpdateRequest;
import com.exadel.sandbox.dto.response.filter.VendorFilterResponse;
import com.exadel.sandbox.dto.response.vendor.VendorDetailsResponse;
import com.exadel.sandbox.dto.response.vendor.VendorShortResponse;

import java.util.List;

public interface VendorDetailsService {

    List<VendorShortResponse> findAllByUserLocation(Long userId);

    VendorDetailsResponse findByIdWithLocations(Long id);

    List<VendorFilterResponse>findAllVendorByLocationFilter(Long id, boolean isCountry);

    List<VendorFilterResponse>findAllVendorByCategoryFilter(List<Long> ids);

    List<VendorFilterResponse>findAllVendorFilter();

    void create(VendorRequest request);

    void update(Long vendorId, VendorUpdateRequest request);
}
