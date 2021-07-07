package com.exadel.sandbox.service.impl;

import com.exadel.sandbox.dto.request.vendor.VendorRequest;
import com.exadel.sandbox.dto.request.vendor.VendorUpdateRequest;
import com.exadel.sandbox.dto.response.filter.VendorFilterResponse;
import com.exadel.sandbox.dto.response.vendor.VendorDetailsResponse;
import com.exadel.sandbox.dto.response.vendor.VendorShortResponse;
import com.exadel.sandbox.mappers.vendor.VendorMapper;
import com.exadel.sandbox.mappers.vendor.VendorShortMapper;
import com.exadel.sandbox.repository.vendor.VendorRepository;
import com.exadel.sandbox.service.CityService;
import com.exadel.sandbox.service.LocationService;
import com.exadel.sandbox.service.VendorDetailsService;
import com.exadel.sandbox.service.exceptions.DuplicateNameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorDetailsServiceImpl implements VendorDetailsService {
    private final VendorRepository repository;
    private final VendorMapper vendorMapper;
    private final VendorShortMapper vendorShortMapper;
    private final CityService cityService;
    private final LocationService locationService;

    @Override
    public List<VendorShortResponse> findAllByUserLocation(Long userId) {
        var cityName = cityService.findCityNameByUserId(userId);
        return repository.findAllByUserLocation(cityName)
                .stream()
                .map(vendorShortMapper::vendorToVendorShortResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VendorDetailsResponse findByIdWithLocations(Long id) {
        return repository.findByIdFetchLocations(id)
                .map(vendorMapper::vendorToVendorDetailsResponse)
                .orElseThrow(() -> new EntityNotFoundException((String.format("Vendor with id %d not found", id))));
    }


    @Override
    public List<VendorFilterResponse> findAllVendorByLocationFilter(Long id, boolean isCountry) {
        return repository.findAllByLocationFilterId(id, isCountry).stream()
                .map(vendorMapper::vendorToVendorFilterResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VendorFilterResponse> findAllVendorByLocationFilterFavorites(Long userId, Long id, boolean isCountry) {
        return repository.findAllByLocationFilterIdFavorites(userId, id, isCountry).stream()
                .map(vendorMapper::vendorToVendorFilterResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VendorFilterResponse> findAllVendorByCategoryFilter(List<Long> ids) {
        return repository.findAllByCategoryFilterIds(ids).stream()
                .map(vendorMapper::vendorToVendorFilterResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VendorFilterResponse> findAllVendorFilter() {
        return repository.findAll().stream()
                .map(vendorMapper::vendorToVendorFilterResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void create(VendorRequest request) {
        checkVendorNameExisting(request.getName());
        var locations = request.getLocationRequests()
                .stream()
                .map(l -> locationService.create(l))
                .collect(Collectors.toSet());
        var vendor = vendorMapper.vendorRequestToVendor(request);
        vendor.getLocations().addAll(locations);
        repository.save(vendor);
    }

    @Override
    public void update(Long vendorId, VendorUpdateRequest request) {
        var vendorFromDB = repository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Not found vendor by id ,k. %d", vendorId)));
        if(!vendorFromDB.getName().equalsIgnoreCase(request.getName())){
            checkVendorNameExisting(request.getName());
        }
        var locations = request.getLocationRequests()
                .stream()
                .map(l -> locationService.update(l))
                .collect(Collectors.toSet());
        var vendor = vendorMapper.vendorUpdateRequestToVendor(request);
        vendor.getLocations().addAll(locations);
        vendor.setId(vendorFromDB.getId());
        repository.save(vendor);
    }

    private void checkVendorNameExisting(String name) {
        if (repository.findByName(name).isPresent()) {
            throw new DuplicateNameException("Vendor already exists");
        }
    }
}