package com.exadel.sandbox.service.impl;

import com.exadel.sandbox.dto.pagelist.PageList;
import com.exadel.sandbox.dto.response.event.CustomEventResponse;
import com.exadel.sandbox.dto.response.event.EventResponseFoOrders;
import com.exadel.sandbox.mail.MailUtil;
import com.exadel.sandbox.mappers.event.EventMapper;
import com.exadel.sandbox.model.vendorinfo.Event;
import com.exadel.sandbox.repository.event.EventRepository;
import com.exadel.sandbox.repository.user.UserOrderRepository;
import com.exadel.sandbox.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_FIELD_SORT = "name";

    private final EventRepository eventRepository;
    private final UserOrderRepository userOrderRepository;
    private final EventMapper eventMapper;
    private final MailUtil mailUtil;

    /*ToDo:  Implement verification by "limitation" and remove PRIMARY*/
    @Override
    public String saveEventToOrder(Long userId, Long eventId, String email) {
        var event = verifyEventId(eventId);
        updateLimitation(event);
        mailUtil.sendSimpleMessage(email);
        userOrderRepository.insertIntoUserOrder(eventId, userId, LocalDateTime.now());
        return "Event successfully saved to User Order ";
    }

    @Override
    public String removeEventFromOrder(Long eventId, Long userId) {
        var exist = eventRepository.getOneEventsFromUserOrder(eventId, userId);
         if (exist==null)
                throw  new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Not Found. EventId: " + eventId + " in User Order");

        userOrderRepository.deleteFromUserOrder(eventId, userId);
        return "Event successfully removed from User Order ";
    }

    @Override
    public PageList<EventResponseFoOrders> getAllFromOrder(Long userId,
                                                           Integer pageNumber, Integer pageSize) {
        final Page<Event> eventsFromOrder = userOrderRepository.getAllEventsFromUserOrder(userId,
                PageRequest.of(getPageNumber(pageNumber), getPageSize(pageSize),
                        Sort.by(Sort.Direction.DESC, "dateEnd")));

        if (eventsFromOrder.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Not Found. Your order list is empty");
        }

        return new PageList<>(
                eventMapper.eventToEventResponseFoOrder(eventsFromOrder.getContent()),
                eventsFromOrder);
    }

    private Event verifyEventId(Long eventId) {
        if (eventId <= 0) {
            throw new IllegalArgumentException("Id is not correct");
        }

        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Not Found. EventId: " + eventId + " in User Order"));

    }

    private void updateLimitation(Event event) {
        int limitation = event.getLimitation();
        if (limitation <= 0) {
            throw new IllegalArgumentException("Limitation less then zero");
        }
        event.setLimitation(--limitation);
        eventRepository.save(event);
    }

    private int getPageNumber(Integer pageNumber) {
        return pageNumber == null || pageNumber < 0 ? DEFAULT_PAGE_NUMBER : pageNumber;
    }

    private int getPageSize(Integer pageSize) {
        return pageSize == null || pageSize < 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }
}
