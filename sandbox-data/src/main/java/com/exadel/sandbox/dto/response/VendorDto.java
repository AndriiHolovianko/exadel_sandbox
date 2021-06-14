package com.exadel.sandbox.dto.response;

import com.exadel.sandbox.model.vendorinfo.Product;
import com.exadel.sandbox.model.vendorinfo.VendorLocation;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VendorDto {
    private Long id;

    private String name;

    private String description;

    private String phoneNumber;

    private String email;
//
//    private Set<Product> products = new HashSet<>();
//
//    private Set<VendorLocation> vendorLocations = new HashSet<>();
}
