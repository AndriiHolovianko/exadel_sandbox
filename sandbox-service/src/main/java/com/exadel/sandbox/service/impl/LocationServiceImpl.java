package com.exadel.sandbox.service.impl;

import com.exadel.sandbox.dto.request.location.LocationRequest;
import com.exadel.sandbox.dto.request.location.VendorLocationRequest;
import com.exadel.sandbox.dto.request.location.VendorLocationUpdateRequest;
import com.exadel.sandbox.dto.request.vendor.VendorRequest;
import com.exadel.sandbox.dto.response.filter.LocationFilterResponse;
import com.exadel.sandbox.dto.response.location.LocationResponse;
import com.exadel.sandbox.model.LocationFilter;
import com.exadel.sandbox.model.location.City;
import com.exadel.sandbox.model.location.Location;
import com.exadel.sandbox.repository.location_repository.LocationRepository;
import com.exadel.sandbox.service.CityService;
import com.exadel.sandbox.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final ModelMapper mapper;
    private final CityService cityService;

    @Override
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    @Override
    public List<Location> findByCity(City city) {
        if (city == null) {
            throw new IllegalArgumentException();
        }

        return locationRepository.findByCity(city);
    }

    @Override
    public List<Location> getAllLocationByCity(String cityName) {
        if (cityName == null) {
            throw new IllegalArgumentException();
        }

        return locationRepository.getLocationsByCityName(cityName);
    }

    @Override
    public LocationResponse create(LocationRequest locationRequest) {
        if (locationRequest == null) {
            throw new IllegalArgumentException();
        }

        var location = mapper.map(
                locationRepository.save(mapper.map(locationRequest, Location.class)),
                LocationResponse.class
        );

        return mapper.map(location, LocationResponse.class);
    }

    @Override
    public LocationResponse update(Long locationId, LocationRequest locationRequest) {
        if (locationRequest == null || locationId == null) {
            throw new IllegalArgumentException();
        }

        final var updatedLocation = mapper.map(locationRequest, Location.class);

        updatedLocation.setId(locationId);

        var location = mapper.map(locationRepository.save(updatedLocation), LocationResponse.class);

        return mapper.map(location, LocationResponse.class);
    }


    @Override
    public List<LocationFilterResponse> findAllLocationFilterByCategoryFilter(List<Long> ids) {
        log.debug(">>>>>>>>" + ids);
        List<LocationFilter> locationFilters=locationRepository.findAllByCategoryFilterIds(ids);
        return groupByCountry(locationRepository.findAllByCategoryFilterIds(ids));
    }

    @Override
    public List<LocationFilterResponse> findAllLocationFilterByVendorFilter(List<Long> ids) {
        log.debug(">>>>>>>>" + ids);
        return groupByCountry(locationRepository.findAllByVendorFilterIds(ids));
    }

    @Override
    public List<LocationFilterResponse> findAllLocationFilter() {
        return groupByCountry(locationRepository.getAllLocationFilter());
    }

    @Override
    public Location update(VendorLocationUpdateRequest request) {
        var locationFromDB = findById(request.getId());
        var city = cityService.findById(request.getCityId());
        var location = getLocation(request, city);
        location.setId(locationFromDB.getId());
        return locationRepository.save(location);
    }

    private List<LocationFilterResponse>groupByCountry(List<LocationFilter> locations){
        List<LocationFilterResponse>filterResponseList=new ArrayList<>();

        for(int i=0;i<locations.size();i++){
            Long currentCountryId=locations.get(i).getCountryId();
            boolean isCountry=true;

            while(true){

                if(!(locations.get(i).getCountryId()==currentCountryId && i<locations.size())){
                    i--;
                    break;
                }
                if(isCountry){
                   filterResponseList.add(
                           transformLocationFilterToLocationFilterResponse(locations.get(i),true));
                    filterResponseList.add(
                            transformLocationFilterToLocationFilterResponse(locations.get(i),false));

                }else{
                    filterResponseList.add(
                            transformLocationFilterToLocationFilterResponse(locations.get(i),false));
                }

                i++;

                if(!(i<locations.size()))
                    break;
                isCountry=false;
            }


        }
        return filterResponseList;
    }

    private LocationFilterResponse transformLocationFilterToLocationFilterResponse(
            LocationFilter locationFilter,boolean isCountry){
        LocationFilterResponse locationFilterResponse = new LocationFilterResponse();

        if(isCountry){
            locationFilterResponse.setId(locationFilter.getCountryId());
            locationFilterResponse.setName(locationFilter.getCountryName());
            locationFilterResponse.setCountry(true);
        }else{
            locationFilterResponse.setId(locationFilter.getCityId());
            locationFilterResponse.setName(locationFilter.getCityName());
            locationFilterResponse.setCountry(false);
        }

        return locationFilterResponse;
    }

    public Location getLocation(VendorLocationUpdateRequest request, City city) {
        return Location.builder().latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .number(request.getNumber())
                .street(request.getStreet())
                .city(city).build();
    }

    @Override
    public Location findById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Not found location by id %d", id)));
    }

    @Override
    public Location create(VendorLocationRequest request) {
        var city = cityService.findById(request.getCityId());
        var location = getLocation(request, city);
        return locationRepository.save(location);
    }

    public Location getLocation(VendorLocationRequest request, City city) {
        return Location.builder().latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .number(request.getNumber())
                .street(request.getStreet())
                .city(city).build();
    }
}
