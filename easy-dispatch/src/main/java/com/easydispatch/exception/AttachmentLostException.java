package com.easydispatch.exception;

public class AttachmentLostException extends BusinessException {
    public AttachmentLostException(String fileName) {
        super(410, "关联的附件文件已丢失: " + fileName + "，请联系管理员");
    }
}
