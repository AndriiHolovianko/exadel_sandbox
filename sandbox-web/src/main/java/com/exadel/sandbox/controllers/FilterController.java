package com.exadel.sandbox.controllers;

import com.exadel.sandbox.dto.request.filter.FilterRequest;
import com.exadel.sandbox.dto.response.filter.FilterResponse;
import com.exadel.sandbox.dto.response.user.AuthenticationResponse;
import com.exadel.sandbox.security.utill.JwtUtil;
import com.exadel.sandbox.service.filter.FilterService;
import com.exadel.sandbox.service.filter.impl.FilterServiceFoFavorites;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/")
@RestController
public class FilterController {

    private final FilterService filterService;
    private final FilterServiceFoFavorites filterServiceFoFavorites;
    private final JwtUtil jwtUtil;

    @PostMapping(produces = {"application/json"},
            consumes = {"application/json"},
            path = {"filtering"})
    public FilterResponse getFilterList(@RequestBody FilterRequest filterRequest,
                                        @RequestHeader("Authorization") AuthenticationResponse authenticationResponse){
        log.debug(">>>>>filtering "+ filterRequest);

        Long userId = jwtUtil.extractUserIdFromAuthResponse(authenticationResponse);

        return filterService.getFilterResponse(filterRequest,userId);

    }

    @PostMapping("filtering/favorites")
    public FilterResponse getFilterListFavorites(@RequestBody FilterRequest filterRequest,
                                        @RequestHeader("Authorization") AuthenticationResponse authenticationResponse){
        log.debug(">>>>>filtering "+ filterRequest);

        Long userId = jwtUtil.extractUserIdFromAuthResponse(authenticationResponse);

        return filterServiceFoFavorites.getFilterResponse(filterRequest,userId);

    }
}
