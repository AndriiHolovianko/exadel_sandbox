package com.exadel.sandbox.repository;

import com.exadel.sandbox.model.vendorinfo.Vendor;

import java.util.List;

public interface VendorRepositoryCustom {
    List<Vendor> findAllByUserCity(Long userId);
}