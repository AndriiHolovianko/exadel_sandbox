package com.exadel.sandbox.dto;

import com.exadel.sandbox.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductDto extends BaseEntity {

    private String name;

    private String description;

    private String link;

    private CategoryDto categoryDto;

    private VendorDto vendorDto;

    private Set<EventDto> eventDtos;
}
