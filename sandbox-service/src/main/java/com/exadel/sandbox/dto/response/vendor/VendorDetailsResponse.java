package com.exadel.sandbox.dto.response.vendor;

import com.exadel.sandbox.dto.response.location.LocationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorDetailsResponse {

    private VendorResponse vendorResponse;

    private Set<LocationResponse> locationResponses;

}