package com.exadel.sandbox.service.impl;

import com.exadel.sandbox.dto.response.event.EventResponse;
import com.exadel.sandbox.dto.response.event.EventShortResponse;
import com.exadel.sandbox.dto.response.user.UserResponse;
import com.exadel.sandbox.mappers.event.EventShortMapper;
import com.exadel.sandbox.mappers.user.UserMapper;
import com.exadel.sandbox.model.user.User;
import com.exadel.sandbox.model.vendorinfo.Event;
import com.exadel.sandbox.repository.UserRepository;
import com.exadel.sandbox.repository.event.EventRepository;
import com.exadel.sandbox.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EventRepository eventRepository;
    private final EventShortMapper eventShortMapper;
    private final ModelMapper mapper;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserResponse findByName(String name) {
        User user = userRepository.findByEmail(name);
        return userMapper.userToUserResponse(user);
    }

    @Override
    public EventShortResponse saveEventToOrder(Long userId, Long eventId) {
        Event event = verifyEventId(eventId);
        userRepository.insertIntoUserOrder(eventId, userId);
        return eventShortMapper.eventToEventShortResponse(event);
    }

    @Override
    public EventShortResponse saveEventToSaved(Long userId, Long eventId) {
        Event event = verifyEventId(eventId);
        userRepository.insertIntoUserSaved(eventId, userId);
        return eventShortMapper.eventToEventShortResponse(event);
    }

    @Override
    public void removeEventFromOrder(Long userId, Long eventId) {
        Event event = verifyEventId(eventId);
        userRepository.deleteFromUserOrder(eventId, userId);
    }

    @Override
    public void removeEventFromSaved(Long userId, Long eventId) {
        Event event = verifyEventId(eventId);
        userRepository.deleteFromUserSaved(eventId, userId);
    }

    @Override
    public List<EventResponse> getAllFromOrder(Long userId) {
        return userRepository.getAllEventsFromUserOrder(userId).stream()
                .map(event -> mapper.map(event, EventResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponse> getAllFromSaved(Long userId) {
        return userRepository.getAllEventsFromUserSaved(userId).stream()
                .map(event -> mapper.map(event, EventResponse.class))
                .collect(Collectors.toList());
    }

    private Event verifyEventId(Long eventId) {
        if (eventId <= 0) {
            throw new IllegalArgumentException("Id is not correct");
        }
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " does not found"));

    }

}
