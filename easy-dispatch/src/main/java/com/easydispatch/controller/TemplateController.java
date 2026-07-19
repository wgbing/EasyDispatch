package com.easydispatch.controller;

import com.easydispatch.model.dto.TemplateRequest;
import com.easydispatch.model.dto.TemplateVO;
import com.easydispatch.service.TemplateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public List<TemplateVO> list() {
        return templateService.listAll();
    }

    @GetMapping("/{id}")
    public TemplateVO get(@PathVariable Long id) {
        return templateService.getById(id);
    }

    @PostMapping
    public ResponseEntity<TemplateVO> create(@Valid @RequestBody TemplateRequest req) {
        TemplateVO vo = templateService.create(req);
        return ResponseEntity.created(URI.create("/api/templates/" + vo.getId())).body(vo);
    }

    @PutMapping("/{id}")
    public TemplateVO update(@PathVariable Long id, @Valid @RequestBody TemplateRequest req) {
        return templateService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        templateService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/attachments")
    public TemplateVO uploadAttachment(@PathVariable Long id,
                                       @RequestParam("file") MultipartFile file) {
        return templateService.addAttachment(id, file);
    }
}
