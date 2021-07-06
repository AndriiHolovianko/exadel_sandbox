package com.exadel.sandbox.repository.location_repository.impl;

import com.exadel.sandbox.model.LocationFilter;
import com.exadel.sandbox.model.vendorinfo.Status;
import com.exadel.sandbox.repository.location_repository.LocationRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LocationRepositoryCustomImpl implements LocationRepositoryCustom {

    private static final String WHERE_EVENT_STATUS = " AND e.status NOT IN('" + Status.EXPIRED.name() + "')";

    private final EntityManager entityManager;

    @Override
    public List<LocationFilter> findAllByCategoryFilterIds(List<Long> ids) {
        String sqlWhere = getWhereCondition(ids, "category_id");

        return entityManager.createNativeQuery(
                "SELECT DISTINCT cn.id as countryId, " +
                        "cn.name as countryName, " +
                        "ct.id as cityId, " +
                        "ct.name as cityName FROM category c " +
                        "INNER JOIN event e on c.id=e.category_id " +
                        "INNER JOIN event_location el on e.id= el.event_id " +
                        "INNER JOIN location l on el.location_id=l.id " +
                        "INNER JOIN city ct on l.city_id=c.id " +
                        "INNER JOIN country cn on ct.country_id=cn.id " +
                        sqlWhere + " ORDER BY cn.name ASC, ct.name ASC ")
                .getResultList();

    }

    @Override
    public List<LocationFilter> findAllByVendorFilterIds(List<Long> ids) {
        String sqlWhere = getWhereCondition(ids, "vendor_id");

        return entityManager.createNativeQuery(
                "SELECT DISTINCT cn.id as countryId, " +
                        "cn.name as countryName, " +
                        "ct.id as cityId, " +
                        "ct.name as cityName FROM vendor v " +
                        "INNER JOIN event e on v.id=e.vendor_id " +
                        "INNER JOIN event_location el on e.id= el.event_id " +
                        "INNER JOIN location l on el.location_id=l.id " +
                        "INNER JOIN city ct on l.city_id=ct.id " +
                        "INNER JOIN country cn on ct.country_id=cn.id " +
                        sqlWhere + " ORDER BY cn.name ASC, ct.name ASC ")
                .getResultList();

    }

    @Override
    public List<LocationFilter> getAllLocationFilter() {

        return entityManager.createNativeQuery(
                "SELECT DISTINCT cn.id as countryId, " +
                        "cn.name as countryName, " +
                        "ct.id as cityId, " +
                        "ct.name as cityName " +
                        "FROM city ct " +
                        "INNER JOIN country cn on ct.country_id=cn.id " +
                        " ORDER BY cn.name ASC, ct.name ASC ", "LocalFilterMapping")
                .getResultList();

    }

    @Override
    public List<LocationFilter> getAllEventsLocationsFromSaved(Long userId) {
        String sqlWhere = getWhereCondition(List.of(userId), "user_id");
        return entityManager.createNativeQuery(
                "SELECT DISTINCT  cn.id as countryId, "+
                        "cn.name as countryName, "+
                        "ct.id as cityId, "+
                        "ct.name as cityName "+
                        "FROM user_order e "+
                        "INNER JOIN event a on e.event_id = a.id "+
                        "INNER JOIN event_location el on a.id = el.event_id "+
                        "INNER JOIN  location l on el.location_id = l.id "+
                        "INNER JOIN city ct on l.city_id = ct.id "+
                        "INNER JOIN country cn on ct.country_id = cn.id "+
                        sqlWhere + " ORDER BY cn.name ASC, ct.name ASC ", "LocalFilterMapping")
                .getResultList();
    }

    private String getWhereCondition(List<Long> ids, String fieldForWhere) {

        if (ids.isEmpty() || ids.size() == 0) {
            return "";
        }

        String result = "WHERE e." + fieldForWhere + " IN (";
        int numberOfElements = 0;

        for (Long id : ids) {
            result += (numberOfElements != 0) ? ", " + id : id;
            numberOfElements++;
        }

        result += ")";

        return result;
    }
}
