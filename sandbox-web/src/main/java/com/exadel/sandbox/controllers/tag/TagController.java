package com.exadel.sandbox.controllers.tag;

import com.exadel.sandbox.dto.pagelist.PageList;
import com.exadel.sandbox.dto.response.tag.TagResponse;
import com.exadel.sandbox.service.TagService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@AllArgsConstructor
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService service;

    @GetMapping
    public PageList<TagResponse> getAll(@RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                        @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        return service.findAll(pageNumber, pageSize);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<List<TagResponse>> getEventById(@PathVariable Long categoryId) {
        var tag = service.getTagById(categoryId);
        return new ResponseEntity<>(tag, HttpStatus.OK);
    }

}
