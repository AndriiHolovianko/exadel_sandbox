package com.exadel.sandbox.dto.mapper.response;

import com.exadel.sandbox.dto.response.VendorDto;
import com.exadel.sandbox.model.vendorinfo.Vendor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VendorDtoMapper {
    public static VendorDto toDto(Vendor vendor) {
        return VendorDto.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .description(vendor.getDescription())
                .phoneNumber(vendor.getPhoneNumber())
                .email(vendor.getEmail())
                .build();
    }
}
