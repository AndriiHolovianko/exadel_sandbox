package com.exadel.sandbox.repository.vendor;

import com.exadel.sandbox.model.vendorinfo.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import java.util.Set;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface VendorRepository extends JpaRepository<Vendor, Long>, VendorRepositoryCustom {

    @Query(value = "SELECT count(e.id) AS eventcount,\n" +
            "       v.id,\n" +
            "       v.name\n" +
            "FROM vendor v\n" +
            "       LEFT JOIN event e ON v.id = e.vendor_id\n" +
            "GROUP BY v.id, v.name", nativeQuery = true)
    Page<VendorProjection> findAllWithEventsCount(Pageable pageable);

    @Query(value = "SELECT *\n" +
            "FROM vendor v\n" +
            "         LEFT JOIN vendor_location vl\n" +
            "                   ON v.id = vl.vendor_id\n" +
            "         LEFT JOIN location l ON vl.location_id = l.id\n" +
            "LEFT JOIN city c ON l.city_id = c.id\n" +
            "LEFT JOIN country c2 ON c.country_id = c2.id\n" +
            "WHERE v.id = ?", nativeQuery = true)
    Optional<Vendor> findByIdFetchLocations(Long id);

    Optional<Vendor> findByName(String name);

    @Query(value = "SELECT v.* FROM vendor v " +
            "JOIN event e on e.vendor_id=v.id " +
            "JOIN saved_event se on se.event_id=e.id " +
            "WHERE se.user_id = ?1", nativeQuery = true)
    Set<Vendor> getAllVendorsFromSaved(Long userId);
}
