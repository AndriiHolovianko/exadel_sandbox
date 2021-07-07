package com.exadel.sandbox.controllers;

import com.exadel.sandbox.dto.response.user.AuthenticationResponse;
import com.exadel.sandbox.security.utill.JwtUtil;
import com.exadel.sandbox.service.FavouriteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class FavouriteController {

    private final FavouriteService favouriteService;
    private final JwtUtil jwtUtil;

    @GetMapping(path = "/allEvents/fromFavorites")
    public ResponseEntity<?> getAllEventsFromUserSaved
            (@RequestHeader("Authorization") AuthenticationResponse authResponse,
             @RequestParam(name = "city_id", required = false) Long cityId,
             @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
             @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return new ResponseEntity<>(favouriteService.getAllFromSaved(
                jwtUtil.extractUserIdFromAuthResponse(authResponse),
                cityId, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping(path = "/getCategory/fromFavorites")
    public ResponseEntity<?> grtCategoryFromSaved(
            @RequestHeader("Authorization") AuthenticationResponse authResponse
    ) {
        return ResponseEntity.ok().body(favouriteService.
                categoriesFromSaved(jwtUtil.extractUserIdFromAuthResponse(authResponse)));
    }

    @GetMapping(path = "/getVendors/fromFavorites")
    public ResponseEntity<?> grtVendorsFromSaved(
            @RequestHeader("Authorization") AuthenticationResponse authResponse
    ) {
        return ResponseEntity.ok().body(favouriteService.
                vendorsFromSaved(jwtUtil.extractUserIdFromAuthResponse(authResponse)));
    }

    @PostMapping(path = "/addEvent/toFavorites")
    public ResponseEntity<?> addEventToSaved(
            @RequestHeader("Authorization") AuthenticationResponse authResponse,
            @RequestParam(name = "eventId") Long eventId) {

        return ResponseEntity.ok()
                .body(favouriteService.saveEventToSaved(
                        jwtUtil.extractUserIdFromAuthResponse(authResponse),
                        eventId));
    }

    @DeleteMapping(path = "/removeEvent/fromFavorites")
    public ResponseEntity<?> removeEventFromSaved(
            @RequestHeader("Authorization") AuthenticationResponse authResponse,
            @RequestParam(name = "eventId") Long eventId) {

        return ResponseEntity.ok().body( favouriteService.removeEventFromSaved(
                jwtUtil.extractUserIdFromAuthResponse(authResponse),
                eventId));
    }


}
