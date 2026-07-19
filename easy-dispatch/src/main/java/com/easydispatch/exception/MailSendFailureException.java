package com.easydispatch.exception;

public class MailSendFailureException extends BusinessException {
    public MailSendFailureException(String reason) {
        super(502, "邮件发送失败: " + reason);
    }
}
