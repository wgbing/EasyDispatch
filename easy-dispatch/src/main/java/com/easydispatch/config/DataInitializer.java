package com.easydispatch.config;

import com.easydispatch.model.dto.TemplateRequest;
import com.easydispatch.model.entity.Template;
import com.easydispatch.repository.TemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 启动时插入示例模板数据（仅在表为空时）
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDemoData(TemplateRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                return;
            }
            log.info("初始化示例模板数据...");

            save(repo, "Photoshop 2024 安装包 (含附件)",
                    "发货通知：您购买的 Photoshop 2024 已发货",
                    "亲，您好！\n感谢购买本店商品。\n附件是您的 Photoshop 2024 软件安装包，请查收。\n如有问题请随时联系我们。");

            save(repo, "Python 全套教程视频 (含附件)",
                    "发货通知：Python 全套教程视频",
                    "亲，您好！\n感谢购买本店商品。\n附件是 Python 全套教程视频压缩包，请查收。");

            save(repo, "普通文本通知 (无附件)",
                    "发货通知：您的订单已完成",
                    "亲，您好！\n您的订单已发货完成，感谢您的支持！");

            log.info("示例模板数据初始化完成，共 {} 条", repo.count());
        };
    }

    private void save(TemplateRepository repo, String name, String subject, String body) {
        Template t = new Template();
        t.setName(name);
        t.setEmailSubject(subject);
        t.setEmailBody(body);
        repo.save(t);
    }
}
