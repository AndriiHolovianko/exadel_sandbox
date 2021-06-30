package com.exadel.sandbox.service.impl;

import com.exadel.sandbox.dto.request.location.LocationRequest;
import com.exadel.sandbox.dto.response.filter.CategoryFilterResponse;
import com.exadel.sandbox.dto.response.filter.LocationFilterResponse;
import com.exadel.sandbox.dto.response.location.LocationResponse;
import com.exadel.sandbox.model.LocationFilter;
import com.exadel.sandbox.model.location.City;
import com.exadel.sandbox.model.location.Location;
import com.exadel.sandbox.repository.location_repository.LocationRepository;
import com.exadel.sandbox.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final ModelMapper mapper;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository, ModelMapper mapper) {
        this.locationRepository = locationRepository;
        this.mapper = mapper;
    }

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

    private List<LocationFilterResponse>groupByCountry(List<LocationFilter> locations){
        List<LocationFilterResponse>filterResponseList=new ArrayList<>();

        for(int i=0;i<locations.size();i++){
            Long currentCountryId=locations.get(i).getCountryId();
            boolean isCountry=true;

            while(locations.get(i).getCountryId()==currentCountryId && i<locations.size()){

                if(isCountry){
                   //if(i>0)i--;
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
            i--;

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
}
