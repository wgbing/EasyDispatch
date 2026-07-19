package com.easydispatch.exception;

public class DuplicateTemplateException extends BusinessException {
    public DuplicateTemplateException(String name) {
        super(409, "模板名称已存在，请修改: " + name);
    }
}
