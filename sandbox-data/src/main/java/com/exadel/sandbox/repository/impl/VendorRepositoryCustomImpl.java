package com.exadel.sandbox.repository.impl;

import com.exadel.sandbox.model.vendorinfo.Vendor;
import com.exadel.sandbox.repository.VendorRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class VendorRepositoryCustomImpl implements VendorRepositoryCustom {
    private final EntityManager entityManager;

    @Override
    public List<Vendor> findAllByUserCity(Long userId) {
        return entityManager.createNativeQuery(
                "SELECT vendor.*\n" +
                        "FROM vendor\n" +
                        "WHERE vendor.id IN\n" +
                        "      (SELECT vendor_location.vendor_id\n" +
                        "       FROM vendor_location\n" +
                        "                JOIN location ON vendor_location.location_id = location.id\n" +
                        "           AND location.city_id = (\n" +
                        "               SELECT location.city_id\n" +
                        "               FROM user\n" +
                        "                        JOIN location ON user.id = ? AND user.location_id = location.id\n" +
                        "           )\n" +
                        "      )", Vendor.class)
                .setParameter(1, userId)
                .getResultList();
    }
}