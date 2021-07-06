package com.exadel.sandbox.mappers.tag;

import com.exadel.sandbox.dto.response.filter.TagFilterResponse;
import com.exadel.sandbox.dto.response.tag.TagResponse;
import com.exadel.sandbox.model.vendorinfo.Tag;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class TagMapper {

    private final ModelMapper mapper;

    public TagResponse tagToTagResponse(Tag tag) {
        return Objects.isNull(tag) ? null : mapper.map(tag, TagResponse.class);
    }

    public Set<TagResponse> setTagToSetTagResponse(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptySet();
        }

        return tags.stream()
                .map(this::tagToTagResponse)
                .collect(Collectors.toSet());
    }

    public List<TagResponse> listTagToListTagResponse(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }

        return tags.stream()
                .map(this::tagToTagResponse)
                .collect(Collectors.toList());
    }

    public TagFilterResponse tagToTagFilterResponse(Tag tag) {
        return Objects.isNull(tag) ? null : mapper.map(tag, TagFilterResponse.class);
    }

}