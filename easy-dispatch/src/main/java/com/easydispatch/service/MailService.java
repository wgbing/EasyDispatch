package com.easydispatch.service;

import com.easydispatch.config.AppProperties;
import com.easydispatch.exception.MailSendFailureException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * 邮件发送服务。
 * 当 easydispatch.mail.mock=true 时，仅打印日志不实际发送；
 * 当 mock=false 且 SMTP 配置完整时，走真实 SMTP。
 */
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final AppProperties props;

    public MailService(JavaMailSender mailSender, AppProperties props) {
        this.mailSender = mailSender;
        this.props = props;
    }

    public void sendWithAttachments(String toEmail,
                                    String subject,
                                    String body,
                                    List<AttachmentFile> attachments) {
        if (props.getMail().isMock()) {
            logMock(toEmail, subject, body, attachments);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(props.getMail().getFrom());
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, false);

            for (AttachmentFile af : attachments) {
                helper.addAttachment(af.displayName, af.file);
            }
            mailSender.send(message);
            log.info("邮件已发送至 {} (主题: {}, 附件数: {})",
                    toEmail, subject, attachments.size());
        } catch (MessagingException e) {
            log.error("邮件发送失败: {}", e.getMessage(), e);
            throw new MailSendFailureException(e.getMessage());
        }
    }

    private void logMock(String toEmail, String subject, String body, List<AttachmentFile> attachments) {
        log.info("==================================================");
        log.info("[MOCK] 发件人      : {}", props.getMail().getFrom());
        log.info("[MOCK] 收件人      : {}", toEmail);
        log.info("[MOCK] 主题        : {}", subject);
        log.info("[MOCK] 正文        : {}", body);
        log.info("[MOCK] 附件数量    : {}", attachments.size());
        for (AttachmentFile af : attachments) {
            log.info("[MOCK]   - {} ({} bytes) -> {}",
                    af.displayName, af.file.length(), af.file.getAbsolutePath());
        }
        log.info("==================================================");
    }

    public static class AttachmentFile {
        public final String displayName;
        public final File file;
        public AttachmentFile(String displayName, Path path) {
            this.displayName = displayName;
            this.file = path.toFile();
        }
    }
}
