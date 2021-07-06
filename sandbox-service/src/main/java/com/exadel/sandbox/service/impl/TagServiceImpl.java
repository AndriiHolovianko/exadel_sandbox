package com.exadel.sandbox.service.impl;

import com.exadel.sandbox.dto.pagelist.PageList;
import com.exadel.sandbox.dto.response.filter.TagFilterResponse;
import com.exadel.sandbox.dto.response.tag.TagResponse;
import com.exadel.sandbox.mappers.tag.TagMapper;
import com.exadel.sandbox.repository.tag.TagRepository;
import com.exadel.sandbox.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_FIELD_SORT = "name";

    private final TagRepository repository;
    private final TagMapper mapper;

    @Override
    public PageList<TagResponse> findAll(Integer pageNumber, Integer pageSize) {
        var page = repository.findAll(PageRequest.of(
                getPageNumber(pageNumber), getPageSize(pageSize), Sort.by(DEFAULT_FIELD_SORT)
        ));
        return new PageList<>(mapper.listTagToListTagResponse(page.getContent()), page);
    }

    @Override
    public List<TagFilterResponse> findAllTagFilter() {
        return repository.findAll().stream()
                .map(mapper::tagToTagFilterResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagFilterResponse> findAllTagsByCategoryFilter(List<Long> ids) {
        return repository.findAllByCategoryFilter(ids).stream()
                .map(mapper::tagToTagFilterResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagResponse> getTagById(Long categoryId) {
        return repository.findTagsByCategoryId(categoryId)
                .stream()
                .map(mapper::tagToTagResponse)
                .collect(Collectors.toList());
    }

    private int getPageNumber(Integer pageNumber) {
        return pageNumber == null || pageNumber < 0 ? DEFAULT_PAGE_NUMBER : pageNumber;
    }

    private int getPageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
    }

}
