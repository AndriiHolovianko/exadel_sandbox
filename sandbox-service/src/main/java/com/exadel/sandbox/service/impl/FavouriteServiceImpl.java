package com.exadel.sandbox.service.impl;

import com.exadel.sandbox.dto.pagelist.PageList;
import com.exadel.sandbox.dto.response.category.CategoryShortResponse;
import com.exadel.sandbox.dto.response.event.CustomEventResponse;
import com.exadel.sandbox.dto.response.location.LocationShortResponse;
import com.exadel.sandbox.dto.response.vendor.VendorShortResponse;
import com.exadel.sandbox.mappers.event.EventMapper;
import com.exadel.sandbox.model.vendorinfo.Event;
import com.exadel.sandbox.repository.category.CategoryRepository;
import com.exadel.sandbox.repository.event.EventRepository;
import com.exadel.sandbox.repository.user.UserRepository;
import com.exadel.sandbox.repository.user.UserSavedRepository;
import com.exadel.sandbox.repository.vendor.VendorRepository;
import com.exadel.sandbox.service.FavouriteService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_FIELD_SORT = "name";

    private final UserSavedRepository userSavedRepository;
    private final VendorRepository vendorRepository;
    private final ModelMapper mapper;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<LocationShortResponse> eventsLocationsFromSaved(Long userId) {
        return null;
    }

    @Override
    public List<VendorShortResponse> vendorsFromSaved(Long userId) {
        return vendorRepository.getAllVendorsFromSaved(userId).stream()
                .map(el -> mapper.map(el, VendorShortResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryShortResponse> categoriesFromSaved(Long userId) {
        return categoryRepository.getAllCategoriesFromSaved(userId).stream()
                .map(el -> mapper.map(el, CategoryShortResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public PageList<CustomEventResponse> getAllFromSaved(Long userId, Long cityId,
                                                         Integer pageNumber, Integer pageSize) {
        final Page<Event> allEventFromSaved = eventRepository.getAllEventsFromUserSaved(userId,
                PageRequest.of(getPageNumber(pageNumber),
                        getPageSize(pageSize)));
        if (cityId == null || cityId == 0) {
            cityId = userRepository.getById(userId).getLocation().getCity().getId();
        }

        return new PageList<>(
                eventMapper.eventListToCustomEventResponseListFavorites(allEventFromSaved.getContent(),
                        cityId),
                allEventFromSaved);
    }

    @Override
    public String saveEventToSaved(Long userId, Long eventId) {
        if (verifyEventId(eventId, userId) != null)
            throw new EntityNotFoundException("Event already exist in Favorites");
        userSavedRepository.insertIntoUserSaved(eventId, userId);
        return "Event successful added to User Favorite";
    }

    @Override
    public String removeEventFromSaved(Long userId, Long eventId) {
        if (verifyEventId(eventId, userId) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Not Found. EventId: " + eventId + " in User Favorites");
        userSavedRepository.deleteFromUserSaved(eventId, userId);
        return "Event successfully removed from User Favorites ";
    }

    private Event verifyEventId(Long eventId, Long userId) {
        if (eventId <= 0) {
            throw new IllegalArgumentException("Id is not correct");
        }
        return eventRepository.getOneEventsFromUserSaved(eventId, userId);
    }

    private int getPageNumber(Integer pageNumber) {
        return pageNumber == null || pageNumber < 0 ? DEFAULT_PAGE_NUMBER : pageNumber;
    }

    private int getPageSize(Integer pageSize) {
        return pageSize == null || pageSize < 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }
}
