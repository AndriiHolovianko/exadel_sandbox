package com.exadel.sandbox.service;

import com.exadel.sandbox.dto.pagelist.PageList;
import com.exadel.sandbox.dto.response.filter.TagFilterResponse;
import com.exadel.sandbox.dto.response.tag.TagResponse;

import java.util.List;

public interface TagService {

    PageList<TagResponse> findAll(Integer pageNumber, Integer pageSize);

    List<TagFilterResponse> findAllTagFilter();

    List<TagFilterResponse>findAllTagsByCategoryFilter(List<Long>ids);

    List<TagResponse> getTagById(Long categoryId);
}