package com.exadel.sandbox.mappers.vendor;

import com.exadel.sandbox.dto.request.vendor.VendorRequest;
import com.exadel.sandbox.dto.request.vendor.VendorUpdateRequest;
import com.exadel.sandbox.dto.response.filter.VendorFilterResponse;
import com.exadel.sandbox.dto.response.location.LocationResponse;
import com.exadel.sandbox.dto.response.vendor.VendorDetailsResponse;
import com.exadel.sandbox.dto.response.vendor.VendorResponse;
import com.exadel.sandbox.mappers.location.LocationMapper;
import com.exadel.sandbox.model.vendorinfo.Vendor;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Component
public class VendorMapper {

    private ModelMapper mapper;
    private LocationMapper locationMapper;

    public Vendor vendorRequestToVendor(VendorRequest vendorRequest) {
        return Objects.isNull(vendorRequest) ? null : mapper.map(vendorRequest, Vendor.class);

    }

    public VendorResponse vendorToVendorResponse(Vendor vendor) {
        return Objects.isNull(vendor) ? null : mapper.map(vendor, VendorResponse.class);
    }

    public VendorFilterResponse vendorToVendorFilterResponse(Vendor vendor) {
        return Objects.isNull(vendor) ? null : mapper.map(vendor, VendorFilterResponse.class);
    }

    public VendorDetailsResponse vendorToVendorDetailsResponse(Vendor vendor) {
        return VendorDetailsResponse.builder()
                .vendorResponse(vendorToVendorResponse(vendor))
                .locationResponses(locationMapper.setLocationToSetLocationResponse(vendor.getLocations()))
                .build();
    }

    public Vendor vendorUpdateRequestToVendor(VendorUpdateRequest request) {
        return Objects.isNull(request) ? null : mapper.map(request, Vendor.class);

    }
}