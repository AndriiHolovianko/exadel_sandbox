package com.exadel.sandbox.service.impl;

import com.exadel.sandbox.dto.mapper.response.VendorDtoMapper;
import com.exadel.sandbox.dto.response.VendorDto;
import com.exadel.sandbox.repository.VendorRepository;
import com.exadel.sandbox.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {
    private final VendorRepository repository;

    @Override
    public List<VendorDto> findAll(Long id) {
        var vendors = repository.findAllByUserCity(id);
        return vendors.stream()
                .map(VendorDtoMapper::toDto)
                .collect(Collectors.toList());
        }
}
