package com.exadel.sandbox.controllers;

import com.exadel.sandbox.dto.pagelist.PageList;
import com.exadel.sandbox.dto.request.EventFilterRequest;
import com.exadel.sandbox.dto.request.event.EventRequest;
import com.exadel.sandbox.dto.response.event.CustomEventResponse;
import com.exadel.sandbox.dto.response.event.EventDetailsResponse;
import com.exadel.sandbox.dto.response.user.AuthenticationResponse;
import com.exadel.sandbox.security.utill.JwtUtil;
import com.exadel.sandbox.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@AllArgsConstructor
@RestController
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;
    private final JwtUtil jwtUtil;

    @GetMapping("/")
    public ResponseEntity<?> getAllEventsByCity(
            @RequestHeader("Authorization") AuthenticationResponse authResponse,
            @RequestParam(name = "city_id", required = false) Long cityId,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false) Integer pageSize
    ) {
        final PageList<CustomEventResponse> events = cityId == null ?
                eventService.getAllEventsByUserId(jwtUtil.extractUserIdFromAuthResponse(authResponse), pageNumber, pageSize) :
                eventService.getAllEventsByCityId(cityId, pageNumber, pageSize);

        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PostMapping("/by_filter")
    public ResponseEntity<?> getAllEventsByFilter(
            @RequestHeader("Authorization") AuthenticationResponse authResponse,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "0") Integer pageSize,
            @RequestBody EventFilterRequest eventFilterRequest
    ) {
        final PageList<CustomEventResponse> events = eventService.getEventsByFilter(
                jwtUtil.extractUserIdFromAuthResponse(authResponse),
                eventFilterRequest,
                pageNumber,
                pageSize);

        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable long eventId) {
        var event = eventService.getEventById(eventId);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @GetMapping("/by_description")
    public ResponseEntity<?> getAllEventsByDescription(
            @RequestHeader("Authorization") AuthenticationResponse authResponse,
            @RequestParam(name = "city_id", required = false) Long cityId,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "0") Integer pageSize
    ) {
        return new ResponseEntity<>(eventService.getAllEventsByDescription(cityId, search, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/events")
    public PageList<EventDetailsResponse> getAllEvents(
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false) Integer pageSize
    ) {
        return eventService.getAll(pageNumber, pageSize);
    }

    @DeleteMapping(path = {"/{eventId}"})
    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") Long eventId) {

        return eventService.deleteEventById(eventId) ?
                ResponseEntity.status(HttpStatus.OK).build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping(produces = {"application/json"},
            consumes = {"application/json"},
            path = {"/newEvent"})
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventRequest eventRequest,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            return ResponseEntity.badRequest().body(getErrorMessages(bindingResult));
        }
        return eventService.createEvent(eventRequest);
    }

    @PutMapping(produces = {"application/json"},
            consumes = {"application/json"},
            path = {"/{eventId}"})
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") Long eventId,
                                         @Valid @RequestBody EventRequest eventRequest,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            return ResponseEntity.badRequest().body(getErrorMessages(bindingResult));
        }
        return eventService.updateEvent(eventId, eventRequest);
    }

    @NotNull
    private List<String> getErrorMessages(BindingResult bindingResult) {
        return bindingResult.getAllErrors().
                stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
    }
}
