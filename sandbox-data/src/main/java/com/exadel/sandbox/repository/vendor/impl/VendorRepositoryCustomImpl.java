package com.exadel.sandbox.repository.vendor.impl;

import com.exadel.sandbox.model.vendorinfo.Status;
import com.exadel.sandbox.model.vendorinfo.Vendor;
import com.exadel.sandbox.repository.vendor.VendorRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class VendorRepositoryCustomImpl implements VendorRepositoryCustom {

    private static final String WHERE_EVENT_STATUS = " AND e.status NOT IN('" + Status.EXPIRED.name() + "')";

    private final EntityManager entityManager;

    @Override
    public List<Vendor> findAllByUserLocation(String city) {
        return entityManager.createNativeQuery(
                "SELECT DISTINCT vendor.*\n" +
                        "FROM vendor\n" +
                        "         LEFT JOIN event e ON vendor.id = e.vendor_id\n" +
                        "         LEFT JOIN event_location el ON e.id = el.event_id\n" +
                        "         LEFT JOIN location l ON el.location_id = l.id\n" +
                        "         LEFT JOIN city c ON l.city_id = c.id\n" +
                        "WHERE e.is_online = TRUE\n" +
                        "   OR c.name = ? " +
                        "ORDER BY name",
                Vendor.class)
                .setParameter(1, city)
                .getResultList();
    }

    @Override
    public List<Vendor> findAllByLocationFilterId(Long id, boolean isCountry) {

        String sqlWhere = "";

        if (id == null) {
            sqlWhere = "WHERE true ";
        } else {
            sqlWhere = (isCountry) ? "WHERE cn.id=?" : "WHERE ct.id=?";
        }
        sqlWhere = (isCountry) ? "WHERE cn.id=?" : "WHERE ct.id=?";

        return entityManager.createNativeQuery(
                "SELECT DISTINCT v.* FROM vendor v " +
                        "INNER JOIN event e on v.id=e.vendor_id " +
                        "INNER JOIN event_location el on e.id= el.event_id " +
                        "INNER JOIN location l on el.location_id=l.id " +
                        "INNER JOIN city ct on l.city_id=ct.id " +
                        "INNER JOIN country cn on ct.country_id=cn.id " +
                        sqlWhere + WHERE_EVENT_STATUS + " ORDER BY v.name ASC ",
                Vendor.class)
                .setParameter(1, id)
                .getResultList();

    }

    @Override
    public List<Vendor> findAllByCategoryFilterIds(List<Long> ids) {

        String sqlWhere = getWhereCondition(ids);

        return entityManager.createNativeQuery(
                "SELECT DISTINCT v.* FROM vendor v " +
                        "INNER JOIN event e on v.id=e.vendor_id " +
                        sqlWhere + " ORDER BY v.name ASC ",
                Vendor.class)
                .getResultList();
    }

    @Override
    public List<Vendor> findAllByLocationFilterIdFavorites(Long userId, Long id, boolean isCountry) {
        String sqlWhere = "";

        if (id == null) {
            sqlWhere = "WHERE true ";
        } else {
            sqlWhere = (isCountry) ? "WHERE cn.id=? AND se.user_id=?" : "WHERE ct.id=? AND se.user_id=?";
        }
        sqlWhere = (isCountry) ? "WHERE cn.id=?" : "WHERE ct.id=?";

        return entityManager.createNativeQuery(
                "SELECT DISTINCT v.* FROM vendor v " +
                        "INNER JOIN event e on v.id=e.vendor_id " +
                        "INNER JOIN event_location el on e.id= el.event_id " +
                        "INNER JOIN location l on el.location_id=l.id " +
                        "INNER JOIN city ct on l.city_id=ct.id " +
                        "INNER JOIN country cn on ct.country_id=cn.id " +
                        sqlWhere + WHERE_EVENT_STATUS + " ORDER BY v.name ASC ",
                Vendor.class)
                .setParameter(1, id)
                .getResultList();
    }

    private String getWhereCondition(List<Long> ids) {

        if (ids.isEmpty() || ids.size() == 0) {
            return "";
        }

        String result = "WHERE e.category_id IN (";
        int numberOfElements = 0;

        for (Long id : ids) {
            result += (numberOfElements != 0) ? ", " + id : id;
            numberOfElements++;
        }

        result += ")";

        return result;
    }

}