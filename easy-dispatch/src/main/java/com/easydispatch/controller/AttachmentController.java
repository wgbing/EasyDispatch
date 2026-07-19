package com.easydispatch.controller;

import com.easydispatch.service.TemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    private final TemplateService templateService;

    public AttachmentController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        templateService.removeAttachment(id);
        return ResponseEntity.noContent().build();
    }
}
