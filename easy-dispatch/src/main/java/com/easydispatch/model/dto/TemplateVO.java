package com.easydispatch.model.dto;

import com.easydispatch.model.entity.Attachment;
import com.easydispatch.model.entity.Template;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateVO {

    private Long id;
    private String name;
    private String emailSubject;
    private String emailBody;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AttachmentVO> attachments;
    /** 附件是否完整（true=全部文件存在，false=有文件丢失） */
    private boolean attachmentsHealthy;

    public static TemplateVO from(Template t, boolean healthy) {
        TemplateVO vo = new TemplateVO();
        vo.id = t.getId();
        vo.name = t.getName();
        vo.emailSubject = t.getEmailSubject();
        vo.emailBody = t.getEmailBody();
        vo.createdAt = t.getCreatedAt();
        vo.updatedAt = t.getUpdatedAt();
        vo.attachmentsHealthy = healthy;
        vo.attachments = t.getAttachments().stream()
                .map(AttachmentVO::from)
                .collect(Collectors.toList());
        return vo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmailSubject() { return emailSubject; }
    public void setEmailSubject(String emailSubject) { this.emailSubject = emailSubject; }
    public String getEmailBody() { return emailBody; }
    public void setEmailBody(String emailBody) { this.emailBody = emailBody; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<AttachmentVO> getAttachments() { return attachments; }
    public void setAttachments(List<AttachmentVO> attachments) { this.attachments = attachments; }
    public boolean isAttachmentsHealthy() { return attachmentsHealthy; }
    public void setAttachmentsHealthy(boolean attachmentsHealthy) { this.attachmentsHealthy = attachmentsHealthy; }

    public static class AttachmentVO {
        private Long id;
        private String originalName;
        private Long fileSize;
        private String fileType;

        public static AttachmentVO from(Attachment a) {
            AttachmentVO vo = new AttachmentVO();
            vo.id = a.getId();
            vo.originalName = a.getOriginalName();
            vo.fileSize = a.getFileSize();
            vo.fileType = a.getFileType();
            return vo;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getOriginalName() { return originalName; }
        public void setOriginalName(String originalName) { this.originalName = originalName; }
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
        public String getFileType() { return fileType; }
        public void setFileType(String fileType) { this.fileType = fileType; }
    }
}
