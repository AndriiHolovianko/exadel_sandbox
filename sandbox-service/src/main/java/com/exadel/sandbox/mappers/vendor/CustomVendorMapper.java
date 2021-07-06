package com.exadel.sandbox.mappers.vendor;

import com.exadel.sandbox.dto.response.vendor.CustomVendorResponse;
import com.exadel.sandbox.repository.vendor.VendorProjection;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class CustomVendorMapper {

    private ModelMapper mapper;

    public CustomVendorResponse vendorProjectionToCustomVendorResponse(VendorProjection projection) {
        return Objects.isNull(projection) ? null : mapper.map(projection, CustomVendorResponse.class);
    }

    public List<CustomVendorResponse> listVendorProjectionsToListCustomVendorResponse(List<VendorProjection> projections) {
        if (projections == null || projections.isEmpty()) {
            return Collections.emptyList();
        }

        return projections.stream()
                .map(this::vendorProjectionToCustomVendorResponse)
                .collect(Collectors.toList());
    }
}
