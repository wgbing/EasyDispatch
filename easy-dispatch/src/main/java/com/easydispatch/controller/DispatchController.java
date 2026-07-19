package com.easydispatch.controller;

import com.easydispatch.model.dto.DispatchRequest;
import com.easydispatch.service.DispatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> dispatch(@Valid @RequestBody DispatchRequest req) {
        dispatchService.dispatch(req);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "邮件发送成功");
        result.put("toEmail", req.getToEmail());
        result.put("templateId", req.getTemplateId());
        return ResponseEntity.ok(result);
    }
}
