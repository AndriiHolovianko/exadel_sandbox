package com.exadel.sandbox.repository.location_repository;

import com.exadel.sandbox.model.location.City;
import com.exadel.sandbox.model.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>, LocationRepositoryCustom {

    List<Location> findAll();

    List<Location> findByCity(City city);

    List<Location> getLocationsByCityName(String cityName);

    @Query("select distinct l from Location l " +
            "where (l.id in (?1))")
    Set<Location> getLocationsById(Set<Long> locationsIds);


//    @Query(value = "SELECT DISTINCT  cn.id as countryId,"+
//            "cn.name as countryName,"+
//            "ct.id as cityId,"+
//            "ct.name as cityName"+
//            "FROM user_order uo"+
//            "INNER JOIN event e on uo.event_id = e.id"+
//            "INNER JOIN event_location el on e.id = el.event_id"+
//            "INNER JOIN  location l on el.location_id = l.id"+
//            "INNER JOIN city ct on l.city_id = ct.id"+
//            "INNER JOIN country cn on ct.country_id = cn.id"+
//            "WHERE uo.user_id = :userId"+
//            "ORDER BY cn.name ASC, ct.name ASC", nativeQuery = true)
//    List<Location> getAllEventsLocationsFromSaved(Long userId);

}
