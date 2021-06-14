package com.exadel.sandbox.ui.mapper.response;

import com.exadel.sandbox.dto.response.VendorDto;
import com.exadel.sandbox.ui.response.VendorShortResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VendorShortResponseMapper {
    public static VendorShortResponse toResponse(VendorDto dto){
        return VendorShortResponse.builder()
                .id(dto.getId())
                .name(dto.getName()).build();
    }
}
