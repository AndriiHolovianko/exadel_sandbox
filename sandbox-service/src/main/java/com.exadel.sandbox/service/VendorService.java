package com.exadel.sandbox.service;

import com.exadel.sandbox.dto.response.VendorDto;
import com.exadel.sandbox.model.vendorinfo.Vendor;

import java.util.List;

public interface VendorService {
    List<VendorDto> findAll(Long id);
}
