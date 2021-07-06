package com.exadel.sandbox.service;

import com.exadel.sandbox.dto.request.location.LocationRequest;
import com.exadel.sandbox.dto.request.location.VendorLocationRequest;
import com.exadel.sandbox.dto.request.location.VendorLocationUpdateRequest;
import com.exadel.sandbox.dto.request.vendor.VendorRequest;
import com.exadel.sandbox.dto.response.filter.LocationFilterResponse;
import com.exadel.sandbox.dto.response.location.LocationResponse;
import com.exadel.sandbox.model.location.City;
import com.exadel.sandbox.model.location.Location;

import java.util.List;

public interface LocationService {

    List<Location> findAll();

    List<Location> findByCity(City city);

    List<Location> getAllLocationByCity(String cityName);

    LocationResponse create(LocationRequest locationRequest);

    LocationResponse update(Long locationId, LocationRequest locationRequest);

    List<LocationFilterResponse> findAllLocationFilterByCategoryFilter(List<Long> ids);

    List<LocationFilterResponse> findAllLocationFilterByVendorFilter(List<Long> ids);

    List<LocationFilterResponse>findAllLocationFilter();

    Location update(VendorLocationUpdateRequest request);

    Location getLocation(VendorLocationUpdateRequest request, City city);

    Location findById(Long id);

    Location create(VendorLocationRequest request);

    Location getLocation(VendorLocationRequest request, City city);

    public List<LocationFilterResponse> findAllLocationFilterFavorites(Long userId);
}
