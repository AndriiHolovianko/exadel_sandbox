package com.exadel.sandbox.service.impl;

import com.exadel.sandbox.dto.pagelist.PageList;
import com.exadel.sandbox.dto.response.vendor.CustomVendorResponse;
import com.exadel.sandbox.dto.response.vendor.VendorShortResponse;
import com.exadel.sandbox.mappers.vendor.CustomVendorMapper;
import com.exadel.sandbox.mappers.vendor.VendorShortMapper;
import com.exadel.sandbox.repository.vendor.VendorRepository;
import com.exadel.sandbox.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_FIELD_SORT = "name";

    private final VendorRepository repository;
    private final VendorShortMapper mapper;
    private final CustomVendorMapper customMapper;

    @Override
    public PageList<VendorShortResponse> findAll(Integer pageNumber, Integer pageSize) {
        var page = repository.findAll(PageRequest.of(
                getPageNumber(pageNumber), getPageSize(pageSize), Sort.by(DEFAULT_FIELD_SORT)
        ));
        return new PageList<>(mapper.listTagToListTagResponse(page.getContent()), page);
    }

    @Override
    public PageList<CustomVendorResponse> findAllWithEventsCount(Integer pageNumber, Integer pageSize) {
        var page = repository.findAllWithEventsCount(PageRequest.of(
                getPageNumber(pageNumber), getPageSize(pageSize), Sort.by(DEFAULT_FIELD_SORT)
        ));
        return new PageList<>(customMapper
                .listVendorProjectionsToListCustomVendorResponse(page.getContent()), page);
    }

    private int getPageNumber(Integer pageNumber) {
        return pageNumber == null || pageNumber < 0 ? DEFAULT_PAGE_NUMBER : pageNumber;
    }

    private int getPageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
    }
}