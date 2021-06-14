package com.exadel.sandbox.controllers;

import com.exadel.sandbox.service.UserService;
import com.exadel.sandbox.service.VendorService;
import com.exadel.sandbox.ui.mapper.response.VendorShortResponseMapper;
import com.exadel.sandbox.ui.response.VendorShortResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vendors")
public class VendorController {
    private final VendorService service;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<VendorShortResponse>> getAllByUserCity() {
        var currentPrincipalName = SecurityContextHolder.getContext().getAuthentication().getName();
        var id = userService.findUserIdByUsername(currentPrincipalName);
        return ResponseEntity.ok(
                service.findAll(id)
                        .stream()
                        .map(VendorShortResponseMapper::toResponse)
                        .collect(Collectors.toList())
        );
    }
}
