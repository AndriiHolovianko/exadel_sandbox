package com.exadel.sandbox.service.impl;

import com.exadel.sandbox.dto.pagelist.PageList;
import com.exadel.sandbox.dto.request.EventFilterRequest;
import com.exadel.sandbox.dto.request.event.EventRequest;
import com.exadel.sandbox.dto.response.event.CustomEventResponse;
import com.exadel.sandbox.dto.response.event.EventDetailsResponse;
import com.exadel.sandbox.mappers.event.EventMapper;
import com.exadel.sandbox.model.location.Location;
import com.exadel.sandbox.model.vendorinfo.Event;
import com.exadel.sandbox.model.vendorinfo.Status;
import com.exadel.sandbox.model.vendorinfo.Tag;
import com.exadel.sandbox.repository.category.CategoryRepository;
import com.exadel.sandbox.repository.event.EventRepository;
import com.exadel.sandbox.repository.location_repository.CityRepository;
import com.exadel.sandbox.repository.location_repository.LocationRepository;
import com.exadel.sandbox.repository.tag.TagRepository;
import com.exadel.sandbox.repository.vendor.VendorRepository;
import com.exadel.sandbox.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class EventServiceImp implements EventService {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_FIELD_SORT = "name";

    private final EventRepository eventRepository;
    private final CityRepository cityRepository;
    private final LocationRepository locationRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;
    private final EventMapper eventMapper;

    @Override
    public PageList<EventDetailsResponse> getAll(Integer pageNumber, Integer pageSize) {
        final Page<Event> eventsPage = eventRepository.findAll(PageRequest.of(getPageNumber(pageNumber),
                getPageSize(pageSize), Sort.by("id").descending()));

        return new PageList<>(eventMapper.eventListToDetailEventResponse(eventsPage.getContent()), eventsPage);
    }

    @Override
    public PageList<CustomEventResponse> getAllEventsByUserId(Long userId, Integer pageNumber, Integer pageSize) {
        var city = cityRepository.findCityByUserId(userId);
        return getEventResponsesByCity(city.getId(), getPageNumber(pageNumber), getPageSize(pageSize));
    }

    @Override
    public PageList<CustomEventResponse> getAllEventsByCityId(Long cityId, Integer pageNumber, Integer pageSize) {
        return getEventResponsesByCity(cityId, getPageNumber(pageNumber), getPageSize(pageSize));
    }

    @Override
    public PageList<CustomEventResponse> getAllEventsByDescription(Long cityId, String search,
                                                                   Integer pageNumber, Integer pageSize) {

        Page<Event> eventsPage = eventRepository.findEventByDescription(("%" + search + "%"),
                cityId, PageRequest.of(getPageNumber(pageNumber), getPageSize(pageSize),
                        Sort.by(Sort.Direction.DESC, "dateEnd")));
        return new PageList<>(eventMapper.
                eventListToCustomEventResponseListByCityId(eventsPage.getContent(), cityId), eventsPage);
    }

    private PageList<CustomEventResponse> getEventResponsesByCity(Long cityId, Integer pageNumber, Integer pageSize) {
        Page<Event> eventsPage = eventRepository.findEventByCityId(cityId,
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dateEnd")));

        return new PageList<>(eventMapper.
                eventListToCustomEventResponseListByCityId(eventsPage.getContent(), cityId), eventsPage);
    }

    @Override
    public EventDetailsResponse getEventById(Long eventId) {
        return Optional.ofNullable(eventRepository.findEventById(eventId))
                .map(eventMapper::eventToEventDetailResponse)
                .orElseThrow(() -> new EntityNotFoundException("entity with id " + eventId + " not found"));
    }

    @Override
    public PageList<CustomEventResponse> getEventsByFilter(Long userId, EventFilterRequest eventFilterRequest,
                                                           Integer pageNumber, Integer pageSize) {
        var sort = getSorting(eventFilterRequest.getStatus());
        pageNumber = getPageNumber(pageNumber);
        pageSize = getPageSize(pageSize);

        if (eventFilterRequest.getLocationId() == 0 &&
                eventFilterRequest.getCategoriesIdSet().isEmpty() &&
                eventFilterRequest.getTagsIdSet().isEmpty() &&
                eventFilterRequest.getVendorsIdSet().isEmpty()) {
            var city = cityRepository.findCityByUserId(userId);

            return getEventResponsesByCity(city.getId(), pageNumber, pageSize);
        }

        if (eventFilterRequest.getLocationId() == 0) {
            var city = cityRepository.findCityByUserId(userId);
            eventFilterRequest.setLocationId(city.getId());
            eventFilterRequest.setCity(true);
        }

        if (!eventFilterRequest.getTagsIdSet().isEmpty() &&
                eventFilterRequest.getVendorsIdSet().isEmpty()) {

            return getCustomEventResponsePageListByTags(eventFilterRequest, pageNumber, pageSize, sort);

        }

        if (!eventFilterRequest.getTagsIdSet().isEmpty() &&
                !eventFilterRequest.getVendorsIdSet().isEmpty()) {

            return getCustomEventResponsePageListByTagsAndVedors(eventFilterRequest, pageNumber, pageSize, sort);
        }

        if (!eventFilterRequest.getCategoriesIdSet().isEmpty() &&
                eventFilterRequest.getTagsIdSet().isEmpty() &&
                eventFilterRequest.getVendorsIdSet().isEmpty()) {

            return getCustomEventResponsePageListByCategories(eventFilterRequest, pageNumber, pageSize, sort);
        }

        if (!eventFilterRequest.getCategoriesIdSet().isEmpty() &&
                eventFilterRequest.getTagsIdSet().isEmpty() &&
                !eventFilterRequest.getVendorsIdSet().isEmpty()) {

            return getCustomEventResponsePageListByCategoriesAndVendors(eventFilterRequest, pageNumber, pageSize, sort);
        }

        if (eventFilterRequest.getCategoriesIdSet().isEmpty() &&
                eventFilterRequest.getTagsIdSet().isEmpty() &&
                !eventFilterRequest.getVendorsIdSet().isEmpty()) {

            return getCustomEventResponsePageListByVendors(eventFilterRequest, pageNumber, pageSize, sort);
        }
        return null;
    }

    private PageList<CustomEventResponse> getCustomEventResponsePageListByVendors
            (EventFilterRequest eventFilterRequest, Integer pageNumber, Integer pageSize, Sort sort) {
        final Page<Event> eventPage;

        if (eventFilterRequest.isCity()) {
            eventPage = eventRepository.findByVendorsByCity(eventFilterRequest.getLocationId(),
                    eventFilterRequest.getVendorsIdSet(),
                    eventFilterRequest.getStatus(),
                    PageRequest.of(pageNumber, pageSize, sort));

            return new PageList<>(eventMapper.eventListToCustomEventResponseListByCityId(eventPage.getContent(),
                    eventFilterRequest.getLocationId()), eventPage);

        } else {
            eventPage = eventRepository.findByByVendorsCountry(eventFilterRequest.getLocationId(),
                    eventFilterRequest.getVendorsIdSet(),
                    eventFilterRequest.getStatus(),
                    PageRequest.of(pageNumber, pageSize, sort));

            return new PageList<>(eventMapper.eventListToCustomEventResponseListByCountryId(eventPage.getContent(),
                    eventFilterRequest.getLocationId()), eventPage);
        }
    }

    private PageList<CustomEventResponse> getCustomEventResponsePageListByCategoriesAndVendors
            (EventFilterRequest eventFilterRequest, Integer pageNumber, Integer pageSize, Sort sort) {
        final Page<Event> eventPage;

        if (eventFilterRequest.isCity()) {
            eventPage = eventRepository.findByCategoryByVendorsByCity(eventFilterRequest.getLocationId(),
                    eventFilterRequest.getVendorsIdSet(),
                    eventFilterRequest.getCategoriesIdSet(),
                    eventFilterRequest.getStatus(),
                    PageRequest.of(pageNumber, pageSize, sort));

            return new PageList<>(eventMapper.eventListToCustomEventResponseListByCityId(eventPage.getContent(),
                    eventFilterRequest.getLocationId()), eventPage);

        } else {
            eventPage = eventRepository.findByCategoryByByVendorsCountry(eventFilterRequest.getLocationId(),
                    eventFilterRequest.getVendorsIdSet(),
                    eventFilterRequest.getCategoriesIdSet(),
                    eventFilterRequest.getStatus(),
                    PageRequest.of(pageNumber, pageSize, sort));

            return new PageList<>(eventMapper.eventListToCustomEventResponseListByCountryId(eventPage.getContent(),
                    eventFilterRequest.getLocationId()), eventPage);
        }
    }

    private PageList<CustomEventResponse> getCustomEventResponsePageListByCategories
            (EventFilterRequest eventFilterRequest, Integer pageNumber, Integer pageSize, Sort sort) {
        final Page<Event> eventPage;

        if (eventFilterRequest.isCity()) {
            eventPage = eventRepository.findByCategoryByCity(eventFilterRequest.getLocationId(),
                    eventFilterRequest.getCategoriesIdSet(),
                    eventFilterRequest.getStatus(),
                    PageRequest.of(pageNumber, pageSize, sort));

            return new PageList<>(eventMapper.eventListToCustomEventResponseListByCityId(eventPage.getContent(),
                    eventFilterRequest.getLocationId()), eventPage);

        } else {
            eventPage = eventRepository.findByCategoryByCountry(eventFilterRequest.getLocationId(),
                    eventFilterRequest.getCategoriesIdSet(),
                    eventFilterRequest.getStatus(),
                    PageRequest.of(pageNumber, pageSize, sort));

            return new PageList<>(eventMapper.eventListToCustomEventResponseListByCountryId(eventPage.getContent(),
                    eventFilterRequest.getLocationId()), eventPage);
        }
    }

    private PageList<CustomEventResponse> getCustomEventResponsePageListByTagsAndVedors
            (EventFilterRequest eventFilterRequest, Integer pageNumber, Integer pageSize, Sort sort) {
        final Page<Event> eventPage;
        if (eventFilterRequest.isCity()) {
            eventPage = eventRepository.findByTagsByVendorsByCity(eventFilterRequest.getLocationId(),
                    eventFilterRequest.getVendorsIdSet(),
                    eventFilterRequest.getTagsIdSet(),
                    eventFilterRequest.getStatus(),
                    PageRequest.of(pageNumber, pageSize, sort));

            return new PageList<>(eventMapper.eventListToCustomEventResponseListByCityId(eventPage.getContent(),
                    eventFilterRequest.getLocationId()), eventPage);

        } else {
            eventPage = eventRepository.findByTagsByVendorsByCountry(eventFilterRequest.getLocationId(),
                    eventFilterRequest.getVendorsIdSet(),
                    eventFilterRequest.getTagsIdSet(),
                    eventFilterRequest.getStatus(),
                    PageRequest.of(pageNumber, pageSize, sort));

            return new PageList<>(eventMapper.eventListToCustomEventResponseListByCountryId(eventPage.getContent(),
                    eventFilterRequest.getLocationId()), eventPage);
        }
    }

    private PageList<CustomEventResponse> getCustomEventResponsePageListByTags(EventFilterRequest eventFilterRequest,
                                                                               Integer pageNumber, Integer pageSize, Sort sort) {
        final Page<Event> eventPage;

        if (eventFilterRequest.isCity()) {
            eventPage = eventRepository.findByTagsByCity(eventFilterRequest.getLocationId(),
                    eventFilterRequest.getTagsIdSet(),
                    eventFilterRequest.getStatus(),
                    PageRequest.of(pageNumber, pageSize, sort));

            return new PageList<>(eventMapper.eventListToCustomEventResponseListByCityId(eventPage.getContent(),
                    eventFilterRequest.getLocationId()), eventPage);

        } else {
            eventPage = eventRepository.findByTagsByCountry(eventFilterRequest.getLocationId(),
                    eventFilterRequest.getTagsIdSet(),
                    eventFilterRequest.getStatus(),
                    PageRequest.of(pageNumber, pageSize, sort));

            return new PageList<>(eventMapper.eventListToCustomEventResponseListByCountryId(eventPage.getContent(),
                    eventFilterRequest.getLocationId()), eventPage);
        }
    }

    private Sort getSorting(Status status) {

        switch (status) {
            case NEW:
                return Sort.by(Sort.Direction.DESC, "dateOfCreation");
            case COMING_SOON:
                return Sort.by(Sort.Direction.ASC, "dateBegin");
            case ACTIVE:
                return Sort.by(Sort.Direction.DESC, "dateEnd");
            case EXPIRED:
                return Sort.by(Sort.Direction.DESC, "name");
            default:
                return Sort.by(Sort.Direction.DESC, "name");
        }
    }

    @Override
    public boolean deleteEventById(Long eventId) {
        final var event = eventRepository.getById(eventId);

        if (checkRightForRemove(event)) return false;

        eventRepository.delete(event);

        return true;
    }

    private boolean checkRightForRemove(Event event) {
        return !event.getUserFeedbacks().isEmpty() ||
                !event.getUserOrders().isEmpty() ||
                !event.getUserSavedEvents().isEmpty();
    }

    @Override
    public ResponseEntity<?> saveEvent(Long vendorId, EventRequest eventRequest) {

        if (vendorId == null || vendorId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        final Set<Location> locationsById = locationRepository.getLocationsById(eventRequest.getLocationsId());
        final Set<Tag> tagsById = tagRepository.getTagsById(eventRequest.getTagsId());
        final var vendor = vendorRepository.getById(vendorId);

        if (locationsById.isEmpty() || tagsById.isEmpty() || vendor == null) {
            return ResponseEntity.badRequest().build();
        }

        final List<Tag> tags = tagsById.stream()
                .filter(tag -> !tag.getCategory().getId().equals(eventRequest.getCategoryId()))
                .collect(Collectors.toList());


        if (!tags.isEmpty()) {
            return ResponseEntity.badRequest().body("Tag's don't agree with the inputted category");
        }

        final var category = categoryRepository.getById(eventRequest.getCategoryId());
        var event = eventMapper.eventRequestToEvent(eventRequest, vendor, locationsById, category, tagsById);

        event = eventRepository.save(event);

        return ResponseEntity.ok().body(eventMapper.eventToEventDetailResponse(event));
    }


    private int getPageNumber(Integer pageNumber) {
        return pageNumber == null || pageNumber < 0 ? DEFAULT_PAGE_NUMBER : pageNumber;
    }

    private int getPageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
    }


}
