package com.easydispatch.service;

import com.easydispatch.exception.AttachmentLostException;
import com.easydispatch.exception.BusinessException;
import com.easydispatch.exception.DuplicateTemplateException;
import com.easydispatch.exception.TemplateNotFoundException;
import com.easydispatch.model.dto.TemplateRequest;
import com.easydispatch.model.dto.TemplateVO;
import com.easydispatch.model.entity.Attachment;
import com.easydispatch.model.entity.Template;
import com.easydispatch.repository.AttachmentRepository;
import com.easydispatch.repository.TemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TemplateService {

    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);

    private final TemplateRepository templateRepository;
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;

    public TemplateService(TemplateRepository templateRepository,
                           AttachmentRepository attachmentRepository,
                           FileStorageService fileStorageService) {
        this.templateRepository = templateRepository;
        this.attachmentRepository = attachmentRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public List<TemplateVO> listAll() {
        return templateRepository.findAll().stream()
                .map(t -> TemplateVO.from(t, checkAttachmentsHealthy(t)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TemplateVO getById(Long id) {
        Template t = templateRepository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException(id));
        return TemplateVO.from(t, checkAttachmentsHealthy(t));
    }

    @Transactional(readOnly = true)
    public Template getEntityById(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException(id));
    }

    @Transactional
    public TemplateVO create(TemplateRequest req) {
        if (templateRepository.existsByName(req.getName())) {
            throw new DuplicateTemplateException(req.getName());
        }
        Template t = new Template();
        t.setName(req.getName());
        t.setEmailSubject(req.getEmailSubject());
        t.setEmailBody(req.getEmailBody());
        t = templateRepository.save(t);
        log.info("创建模板: id={}, name={}", t.getId(), t.getName());
        return TemplateVO.from(t, true);
    }

    @Transactional
    public TemplateVO update(Long id, TemplateRequest req) {
        Template t = getEntityById(id);
        if (templateRepository.existsByNameAndIdNot(req.getName(), id)) {
            throw new DuplicateTemplateException(req.getName());
        }
        t.setName(req.getName());
        t.setEmailSubject(req.getEmailSubject());
        t.setEmailBody(req.getEmailBody());
        t = templateRepository.save(t);
        log.info("更新模板: id={}, name={}", t.getId(), t.getName());
        return TemplateVO.from(t, checkAttachmentsHealthy(t));
    }

    @Transactional
    public void delete(Long id) {
        Template t = getEntityById(id);
        // 先清理物理文件
        for (Attachment a : t.getAttachments()) {
            fileStorageService.delete(a.getStoredPath());
        }
        templateRepository.delete(t);
        log.info("删除模板: id={}", id);
    }

    @Transactional
    public TemplateVO addAttachment(Long templateId, MultipartFile file) {
        Template t = getEntityById(templateId);

        String storedPath;
        try {
            storedPath = fileStorageService.store(file);
        } catch (IOException e) {
            log.error("附件保存失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "附件保存失败: " + e.getMessage());
        }

        Attachment a = new Attachment();
        a.setTemplate(t);
        a.setOriginalName(file.getOriginalFilename());
        a.setStoredPath(storedPath);
        a.setFileSize(file.getSize());
        a.setFileType(FileStorageService.extractType(file.getOriginalFilename()));
        t.getAttachments().add(a);
        attachmentRepository.save(a);
        log.info("模板 {} 添加附件: {}", templateId, a.getOriginalName());
        return TemplateVO.from(t, checkAttachmentsHealthy(t));
    }

    @Transactional
    public void removeAttachment(Long attachmentId) {
        Attachment a = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new BusinessException(404, "附件不存在: id=" + attachmentId));
        Long templateId = a.getTemplate().getId();
        fileStorageService.delete(a.getStoredPath());
        attachmentRepository.delete(a);
        log.info("模板 {} 删除附件: {}", templateId, a.getOriginalName());
    }

    /**
     * 校验模板所有附件文件是否存在
     */
    public boolean checkAttachmentsHealthy(Template t) {
        for (Attachment a : t.getAttachments()) {
            if (!fileStorageService.exists(a.getStoredPath())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 发货前严格校验：若有附件丢失，抛异常
     */
    public void validateForDispatch(Template t) {
        for (Attachment a : t.getAttachments()) {
            if (!fileStorageService.exists(a.getStoredPath())) {
                throw new AttachmentLostException(a.getOriginalName());
            }
        }
    }
}
