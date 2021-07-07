package com.exadel.sandbox.repository.category.impl;

import com.exadel.sandbox.model.vendorinfo.Category;
import com.exadel.sandbox.model.vendorinfo.Status;
import com.exadel.sandbox.repository.category.CategoryRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryCustomImpl implements CategoryRepositoryCustom {

    private static final String WHERE_EVENT_STATUS = " AND e.status NOT IN('" + Status.EXPIRED.name() + "')";

    private final EntityManager entityManager;

    @Override
    public List<Category> findAllByVendorId(Long vendorId) {
        return entityManager.createNativeQuery(
                "SELECT DISTINCT category.*\n" +
                        "FROM category\n" +
                        "         JOIN event ON category.id = event.category_id\n" +
                        "WHERE vendor_id = ?\n" +
                        "ORDER BY name",
                Category.class)
                .setParameter(1, vendorId)
                .getResultList();
    }

    @Override
    public List<Category> findAllByLocationFilterId(Long id, boolean isCountry) {

        String sqlWhere = "";
        if (id == null) {
            sqlWhere = "WHERE true ";
        } else {
            sqlWhere = (isCountry) ? "WHERE cn.id=?" : "WHERE ct.id=?";
        }

        return entityManager.createNativeQuery(
                "SELECT DISTINCT c.* FROM category c " +
                        "INNER JOIN event e on c.id=e.category_id " +
                        "INNER JOIN event_location el on e.id= el.event_id " +
                        "INNER JOIN location l on el.location_id=l.id " +
                        "INNER JOIN city ct on l.city_id=ct.id " +
                        "INNER JOIN country cn on ct.country_id=cn.id " +
                        sqlWhere + WHERE_EVENT_STATUS + " ORDER BY c.name ASC ",
                Category.class)
                .setParameter(1, id)
                .getResultList();
    }

    @Override
    public List<Category> findAllByLocationFilterIdFavorites(Long userId, Long id, boolean isCountry) {

        String sqlWhere = "";

        if (id == null) {
            sqlWhere = "WHERE true ";
        } else {
            sqlWhere = (isCountry) ? "WHERE cn.id=? and se.user_id=?" : "WHERE ct.id=? and se.user_id=?";
        }

        return entityManager.createNativeQuery(
                "SELECT DISTINCT c.* FROM category c " +
                        "INNER JOIN event e on c.id=e.category_id " +
                        "INNER JOIN saved_event se on se.event_id=e.id "+
                        "INNER JOIN event_location el on e.id= el.event_id " +
                        "INNER JOIN location l on el.location_id=l.id " +
                        "INNER JOIN city ct on l.city_id=ct.id " +
                        "INNER JOIN country cn on ct.country_id=cn.id " +
                        sqlWhere + WHERE_EVENT_STATUS +" ORDER BY c.name ASC ",
                Category.class)
                .setParameter(1, id)
                .setParameter(2, userId)
                .getResultList();
    }


    @Override
    public List<Category> findAllByVendorFilterIds(List<Long> ids) {

        String sqlWhere = getWhereConditionForIds(ids);

        return entityManager.createNativeQuery(
                "SELECT DISTINCT c.* FROM category c " +
                        "INNER JOIN event e on c.id=e.category_id " +
                        sqlWhere + WHERE_EVENT_STATUS + " ORDER BY c.name ASC ",
                Category.class)
                .getResultList();
    }


    private String getWhereConditionForIds(List<Long> ids) {

        if (ids.isEmpty() || ids.size() == 0) {
            return "WHERE true";
        }

        String result = "WHERE e.vendor_id IN (";
        int numberOfElements = 0;

        for (Long id : ids) {
            result += (numberOfElements != 0) ? ", " + id : id;
            numberOfElements++;
        }

        result += ") ";

        return result;
    }
}
