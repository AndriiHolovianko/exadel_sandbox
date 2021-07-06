package com.exadel.sandbox.service.impl;

import com.exadel.sandbox.dto.request.CityRequest;
import com.exadel.sandbox.dto.response.city.CityResponse;
import com.exadel.sandbox.model.location.City;
import com.exadel.sandbox.model.vendorinfo.Status;
import com.exadel.sandbox.repository.location_repository.CityRepository;
import com.exadel.sandbox.service.CityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final ModelMapper mapper;

    @Autowired
    public CityServiceImpl(CityRepository cityRepository, ModelMapper mapper) {
        this.cityRepository = cityRepository;
        this.mapper = mapper;
    }

    @Override
    public CityResponse create(CityRequest cityRequest) {
        if (cityRequest == null) {
            throw new IllegalArgumentException();
        }
        return mapper.map(cityRepository.save(
                mapper.map(cityRequest, City.class)),
                CityResponse.class);
    }

    @Override
    public City findById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Not found city by id %d", id)));
    }

    @Override
    public String findCityNameByUserId(Long userId) {
        return cityRepository.findCityNameByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("City with userId %d not found", userId)));
    }

    @Override
    public List<City> findAll() {
        return cityRepository.findAll(Sort.by("name"));
    }

    @Override
    public List<City> findCitiesByCountryNameOrderByName(String countryName) {
        return cityRepository.findCitiesByCountryNameOrderByName(countryName);
    }

    @Override
    public Set<CityResponse> findCitiesByEventStatusActive() {
        return cityRepository.findCitiesByEventStatus(Status.ACTIVE)
                .stream()
                .map(city -> mapper.map(city, CityResponse.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<CityResponse> findCitiesByFavoriteEvent(Long userId) {
        return cityRepository.findCitiesByFavoriteEvents(userId)
                .stream()
                .map(city -> mapper.map(city, CityResponse.class))
                .collect(Collectors.toSet());
    }

    @Override
    public CityResponse findCityByUserId(Long userId) {
        return mapper.map(cityRepository.findCityByUserId(userId), CityResponse.class);
    }
}
