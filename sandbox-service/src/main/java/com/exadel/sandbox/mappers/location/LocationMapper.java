package com.exadel.sandbox.mappers.location;

import com.exadel.sandbox.dto.response.city.CustomCityResponse;
import com.exadel.sandbox.dto.response.location.CustomLocationResponse;
import com.exadel.sandbox.dto.response.location.LocationResponse;
import com.exadel.sandbox.dto.response.location.LocationShortResponse;
import com.exadel.sandbox.model.location.Location;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class LocationMapper {

    private final ModelMapper mapper;

    public LocationResponse locationToLocationResponse(Location location) {
        return Objects.isNull(location) ? null : mapper.map(location, LocationResponse.class);
    }

    public Set<LocationResponse> setLocationToSetLocationResponse(Set<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            return Collections.emptySet();
        }

        return locations.stream()
                .map(this::locationToLocationResponse)
                .collect(Collectors.toSet());
    }

    public LocationShortResponse locationToLocationShortResponse(Location location) {
        if (location == null) {
            return null;
        }

        var locationShortResponse = mapper.map(location, LocationShortResponse.class);
        locationShortResponse.setCountryName(location.getCity().getCountry().getName());

        return locationShortResponse;
    }

    public Set<LocationShortResponse> setLocationToListShortLocation(Set<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            return Collections.emptySet();
        }

        return locations.stream()
                .map(this::locationToLocationShortResponse)
                .collect(Collectors.toSet());
    }

    public Set<LocationShortResponse> setLocationToListCustomLocationResponse(Set<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            return Collections.emptySet();
        }

        return locations.stream()
                .map(this::locationToLocationShortResponse)
                .collect(Collectors.toSet());
    }

    public CustomLocationResponse convertLocToCustomLocResponseByCity(Set<Location> locations, Long cityId) {
        final var locResponseByCity = new CustomLocationResponse();

        final var location = getLocationByCityId(locations, cityId);

        locResponseByCity.setCountryName(location.getCity().getCountry().getName());
        final var customCityResponse = getCustomCityResponse(locations, location, cityId);

        locResponseByCity.getCities().add(customCityResponse);

        return locResponseByCity;
    }

    private Location getLocationByCityId(Set<Location> locations, Long cityId) {
        return locations.stream()
                .filter(loc -> loc.getCity().getId().equals(cityId))
                .findFirst()
                .orElseThrow();
    }

    private Set<String> getAddresses(Set<Location> locations, Long cityId) {
        return locations.stream()
                .filter(loc -> loc.getCity().getId().equals(cityId))
                .map(loc -> loc.getStreet() + " " + loc.getNumber())
                .collect(Collectors.toSet());
    }

    private CustomCityResponse getCustomCityResponse(Set<Location> locations, Location location, Long cityId) {
        return CustomCityResponse.builder()
                .cityName(location.getCity().getName())
                .addresses(getAddresses(locations, cityId))
                .build();
    }

    public CustomLocationResponse convertLocToCustomLocResponseByCountry(Set<Location> locations, Long countryId) {
        final var locResponseByCity = new CustomLocationResponse();

        locResponseByCity.setCountryName(getCountryNameByCountryId(locations, countryId));

        final Map<String, Set<String>> citiesMap = getCitiesMap(locations, countryId);

        locResponseByCity.setCities(getCustomCityResponses(citiesMap));

        return locResponseByCity;
    }

    private String getCountryNameByCountryId(Set<Location> locations, Long countryId) {
        return locations.stream()
                .filter(loc -> loc.getCity().getCountry().getId().equals(countryId))
                .map(loc -> loc.getCity().getCountry().getName())
                .findFirst()
                .orElseThrow();
    }

    private Map<String, Set<String>> getCitiesMap(Set<Location> locations, Long countryId) {
        return locations.stream()
                .filter(loc -> loc.getCity().getCountry().getId().equals(countryId))
                .collect(Collectors.groupingBy(loc -> loc.getCity().getName(),
                        Collectors.mapping(loc -> loc.getStreet() + " " + loc.getNumber(), Collectors.toSet())));
    }

    private Set<CustomCityResponse> getCustomCityResponses(Map<String, Set<String>> collect) {
        Set<CustomCityResponse> customCityResponses = new HashSet<>();

        collect.forEach((k, v) -> customCityResponses.add(convertMapEntryToCustomCityResponse(k, v)));

        return customCityResponses;
    }

    private CustomCityResponse convertMapEntryToCustomCityResponse(String name, Set<String> addresses) {
        return CustomCityResponse.builder()
                .cityName(name)
                .addresses(addresses)
                .build();
    }

}
