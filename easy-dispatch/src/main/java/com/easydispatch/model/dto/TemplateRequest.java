package com.easydispatch.model.dto;

import jakarta.validation.constraints.NotBlank;

public class TemplateRequest {

    @NotBlank(message = "模板名称不能为空")
    private String name;

    @NotBlank(message = "邮件主题不能为空")
    private String emailSubject;

    @NotBlank(message = "邮件正文不能为空")
    private String emailBody;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmailSubject() { return emailSubject; }
    public void setEmailSubject(String emailSubject) { this.emailSubject = emailSubject; }
    public String getEmailBody() { return emailBody; }
    public void setEmailBody(String emailBody) { this.emailBody = emailBody; }
}
