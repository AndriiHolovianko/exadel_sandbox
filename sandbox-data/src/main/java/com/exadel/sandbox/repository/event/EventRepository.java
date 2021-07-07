package com.exadel.sandbox.repository.event;

import com.exadel.sandbox.model.vendorinfo.Event;
import com.exadel.sandbox.model.vendorinfo.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

    @Query("select distinct e from Event e " +
            "join e.locations loc " +
            "where loc.city.id = ?1 " +
            "and e.status = ?2")
    Page<Event> findEventByCityIdAndStatus(Long cityId, Status status, Pageable pageable);

    @Query("select distinct e from Event e " +
            "join e.locations loc " +
            "where loc.city.id = ?2 AND (e.description like ?1 or e.fullDescription like ?1)")
    Page<Event> findEventByDescription(String search, Long cityId, Pageable pageable);

    @Query(value = "SELECT e.* FROM event e " +
            "join saved_event se on e.id = se.event_id " +
            "WHERE event_id=:eventId and user_id =:userId LIMIT 1", nativeQuery = true)
    Event getOneEventsFromUserSaved(@Param("eventId") Long eventId, @Param("userId") Long userId);

    @Query(value = "SELECT e.* FROM event e " +
            "join user_order uo on e.id = uo.event_id " +
            "WHERE event_id=:eventId and user_id =:userId LIMIT 1", nativeQuery = true)
    Event getOneEventsFromUserOrder(@Param("eventId") Long eventId, @Param("userId") Long userId);

    Event findEventById(Long id);

    @Query("select distinct e from Event e " +
            "join e.tags tag " +
            "join e.locations loc " +
            "join loc.city city " +
            "join city.country country " +
            "where (tag.id in (?2)) " +
            "and country.id = ?1 " +
            "and e.status = ?3")
    Page<Event> findByTagsByCountry(Long locationId, Set<Long> tagsIsSet, Status status, PageRequest of);

    @Query("select distinct e from Event e " +
            "join e.tags tag " +
            "join e.locations loc " +
            "join loc.city city " +
            "where (tag.id in (?2)) " +
            "and city.id = ?1 " +
            "and e.status =?3")
    Page<Event> findByTagsByCity(Long locationId, Set<Long> tagsIs, Status status, PageRequest of);

    @Query("select distinct e from Event e " +
            "join e.tags tag " +
            "join e.locations loc " +
            "join loc.city city " +
            "join city.country country " +
            "where (tag.id in (?3)) " +
            "and country.id = ?1 " +
            "and e.vendor.id in (?2) " +
            "and e.status =?4")
    Page<Event> findByTagsByVendorsByCountry(Long locationId, Set<Long> vendorsId, Set<Long> tagsIs, Status status, PageRequest of);

    @Query("select distinct e from Event e " +
            "join e.tags tag " +
            "join e.locations loc " +
            "where (tag.id in (?3)) " +
            "and loc.city.id = ?1 " +
            "and e.vendor.id in (?2) " +
            "and e.status =?4")
    Page<Event> findByTagsByVendorsByCity(Long locationId, Set<Long> vendorsId, Set<Long> tagsIs, Status status, PageRequest of);

    @Query("select distinct e from Event e " +
            "join e.category cat " +
            "join e.locations loc " +
            "join loc.city city " +
            "join city.country country " +
            "where (cat.id in (?2)) " +
            "and country.id = ?1 " +
            "and e.status =?3")
    Page<Event> findByCategoryByCountry(Long locationId, Set<Long> categoriesId, Status status, PageRequest of);

    @Query("select distinct e from Event e " +
            "join e.category cat " +
            "join e.locations loc " +
            "join loc.city city " +
            "where (cat.id in (?2)) " +
            "and city.id = ?1 " +
            "and e.status =?3")
    Page<Event> findByCategoryByCity(Long locationId, Set<Long> categoriesId, Status status, PageRequest of);

    @Query("select distinct e from Event e " +
            "join e.category cat " +
            "join e.locations loc " +
            "join loc.city city " +
            "where (cat.id in (?3)) " +
            "and city.id = ?1 " +
            "and e.vendor.id in (?2) " +
            "and e.status =?3")
    Page<Event> findByCategoryByVendorsByCity(Long locationId, Set<Long> vendorsId, Set<Long> categoriesId, Status status, PageRequest of);

    @Query("select distinct e from Event e " +
            "join e.category cat " +
            "join e.locations loc " +
            "join loc.city city " +
            "join city.country country " +
            "where (cat.id in (?3)) " +
            "and country.id = ?1 " +
            "and e.vendor.id in (?2) " +
            "and e.status =?3")
    Page<Event> findByCategoryByByVendorsCountry(Long locationId, Set<Long> vendorsId, Set<Long> categoriesId, Status status, PageRequest of);

    @Query("select distinct e from Event e " +
            "join e.locations loc " +
            "join loc.city city " +
            "where e.vendor.id in (?2) " +
            "and city.id = ?1 " +
            "and e.status =?3")
    Page<Event> findByVendorsByCity(Long locationId, Set<Long> vendorsId, Status status, PageRequest of);

    @Query("select distinct e from Event e " +
            "join e.category cat " +
            "join e.locations loc " +
            "join loc.city city " +
            "where e.vendor.id in (?2) " +
            "and city.country.id = ?1 " +
            "and e.status =?3")
    Page<Event> findByByVendorsCountry(Long locationId, Set<Long> vendorsId, Status status, PageRequest of);
}
