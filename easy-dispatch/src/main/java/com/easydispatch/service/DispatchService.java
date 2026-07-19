package com.easydispatch.service;

import com.easydispatch.model.dto.DispatchRequest;
import com.easydispatch.model.entity.Attachment;
import com.easydispatch.model.entity.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 发货服务：将模板（正文 + 附件）通过邮件发出
 */
@Service
public class DispatchService {

    private static final Logger log = LoggerFactory.getLogger(DispatchService.class);

    private final TemplateService templateService;
    private final MailService mailService;
    private final FileStorageService fileStorageService;

    public DispatchService(TemplateService templateService,
                           MailService mailService,
                           FileStorageService fileStorageService) {
        this.templateService = templateService;
        this.mailService = mailService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public void dispatch(DispatchRequest req) {
        Template t = templateService.getEntityById(req.getTemplateId());

        // 发货前严格校验附件完整性
        templateService.validateForDispatch(t);

        List<MailService.AttachmentFile> files = t.getAttachments().stream()
                .map(this::toAttachmentFile)
                .collect(Collectors.toList());

        mailService.sendWithAttachments(
                req.getToEmail(),
                t.getEmailSubject(),
                t.getEmailBody(),
                files
        );

        log.info("发货成功: 模板={}, 收件人={}, 附件数={}",
                t.getName(), req.getToEmail(), files.size());
    }

    private MailService.AttachmentFile toAttachmentFile(Attachment a) {
        return new MailService.AttachmentFile(
                a.getOriginalName(),
                fileStorageService.resolve(a.getStoredPath())
        );
    }
}
