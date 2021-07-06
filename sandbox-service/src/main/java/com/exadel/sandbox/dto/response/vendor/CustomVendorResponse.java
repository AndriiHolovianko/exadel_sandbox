package com.exadel.sandbox.dto.response.vendor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomVendorResponse {

    private Long id;

    private String name;

    private int eventCount;

}
