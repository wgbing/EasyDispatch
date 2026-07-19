package com.easydispatch.exception;

public class TemplateNotFoundException extends BusinessException {
    public TemplateNotFoundException(Long id) {
        super(404, "模板不存在: id=" + id);
    }
}
