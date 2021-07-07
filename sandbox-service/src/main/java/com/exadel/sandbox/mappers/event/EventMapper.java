package com.exadel.sandbox.mappers.event;

import com.exadel.sandbox.dto.request.event.EventRequest;
import com.exadel.sandbox.dto.response.event.CustomEventResponse;
import com.exadel.sandbox.dto.response.event.EventDetailsResponse;
import com.exadel.sandbox.dto.response.event.EventResponse;
import com.exadel.sandbox.dto.response.event.EventResponseFoOrders;
import com.exadel.sandbox.dto.response.location.CustomLocationResponse;
import com.exadel.sandbox.mappers.location.LocationMapper;
import com.exadel.sandbox.mappers.tag.TagMapper;
import com.exadel.sandbox.mappers.vendor.VendorShortMapper;
import com.exadel.sandbox.model.location.Location;
import com.exadel.sandbox.model.vendorinfo.Category;
import com.exadel.sandbox.model.vendorinfo.Event;
import com.exadel.sandbox.model.vendorinfo.Tag;
import com.exadel.sandbox.model.vendorinfo.Vendor;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class EventMapper {

    private final ModelMapper mapper;
    private final LocationMapper locMapper;
    private final TagMapper tagMapper;
    private final VendorShortMapper vendorShortMapper;

    public Event eventRequestToEvent(EventRequest eventRequest, Vendor vendor,
                                     Set<Location> locations, Category category, Set<Tag> tags) {

        return Event.builder()
                .description(eventRequest.getDescription())
                .fullDescription(eventRequest.getFullDescription())
                .dateBegin(eventRequest.getDateBegin())
                .dateEnd(eventRequest.getDateEnd())
                .dateOfCreation(LocalDateTime.now())
                .isOnline(eventRequest.isOnline())
                .locations(locations)
                .vendor(vendor)
                .category(category)
                .tags(tags)
                .status(eventRequest.getStatus())
                .build();
    }

    public EventResponse eventToEventResponse(Event event) {
        return Objects.isNull(event) ? null : mapper.map(event, EventResponse.class);
    }

    public EventDetailsResponse eventToEventDetailResponse(Event event) {
        return EventDetailsResponse.builder()
                .id(event.getId())
                .description(event.getDescription())
                .vendorName(event.getVendor().getName())
                .vendorId(event.getVendor().getId())
                .categoryName(event.getCategory().getName())
                .categoryId(event.getCategory().getId())
                .dateBegin(event.getDateBegin())
                .dateEnd(event.getDateEnd())
                .tags(tagMapper.setTagToSetTagResponse(event.getTags()))
                .locations(locMapper.setLocationToSetLocationResponse(event.getLocations()))
                .fullDescription(event.getDescription())
                .build();
    }

    public List<EventResponse> eventListToEventResponseList(List<Event> events) {
        return events.stream()
                .sorted(Comparator.comparing(Event::getDateEnd))
                .map(event -> EventResponse.builder()
                        .id(event.getId())
                        .shortDescription(event.getDescription())
                        .vendorName(event.getVendor().getName())
                        .vendorId(event.getVendor().getId())
                        .locations(locMapper.setLocationToListCustomLocationResponse(event.getLocations()))
                        .dateBegin(event.getDateBegin())
                        .dateEnd(event.getDateEnd())
                        .dateBegin(event.getDateBegin())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CustomEventResponse> eventListToCustomEventResponseListByCityId(List<Event> events, Long cityId) {
        return eventListToCustomEventResponseListId(locMapper::convertLocToCustomLocResponseByCity, events, cityId);
    }

    public List<CustomEventResponse> eventListToCustomEventResponseListByCountryId(List<Event> events, Long countryId) {
        return eventListToCustomEventResponseListId(locMapper::convertLocToCustomLocResponseByCountry, events, countryId);
    }

    public List<CustomEventResponse> eventListToCustomEventResponseListId(BiFunction<Set<Location>, Long, CustomLocationResponse> biFunction,
                                                                          List<Event> events,
                                                                          Long id) {
        return events.stream()
                .map(event -> CustomEventResponse.builder()
                        .id(event.getId())
                        .shortDescription(event.getDescription())
                        .vendorName(event.getVendor().getName())
                        .vendorId(event.getVendor().getId())
                        .locations(biFunction.apply(event.getLocations(), id))
                        .categoryId(event.getCategory().getId())
                        .categoryName(event.getCategory().getName())
                        .dateBegin(event.getDateBegin())
                        .dateEnd(event.getDateEnd())
                        .dateBegin(event.getDateBegin())
                        .build())
                .collect(Collectors.toList());
    }

    public List<EventDetailsResponse> eventListToDetailEventResponse(List<Event> events) {
        return events.stream()
                .map(event -> mapper.map(event, EventDetailsResponse.class))
                .collect(Collectors.toList());
    }

    public List<EventResponseFoOrders> eventToEventResponseFoOrder(List<Event> events) {
        return events.stream()
                .map(event -> EventResponseFoOrders.builder()
                        .id(event.getId())
                        .gettingDate(event.getDateOfCreation())
                        .description(event.getDescription())
                        .vendorShortResponse(vendorShortMapper.vendorToVendorShortResponse(event.getVendor()))
                        .dateEnd(event.getDateEnd())
                        .locations(locMapper.setLocationToListShortLocation(event.getLocations()))
                        .build())
                .collect(Collectors.toList());
    }

}
