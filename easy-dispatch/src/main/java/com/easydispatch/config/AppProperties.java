package com.easydispatch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "easydispatch")
public class AppProperties {

    private Storage storage = new Storage();
    private Mail mail = new Mail();
    private Upload upload = new Upload();

    public Storage getStorage() { return storage; }
    public void setStorage(Storage storage) { this.storage = storage; }
    public Mail getMail() { return mail; }
    public void setMail(Mail mail) { this.mail = mail; }
    public Upload getUpload() { return upload; }
    public void setUpload(Upload upload) { this.upload = upload; }

    public static class Storage {
        private String root = "./data/attachments";
        public String getRoot() { return root; }
        public void setRoot(String root) { this.root = root; }
    }

    public static class Mail {
        private String from = "noreply@easydispatch.local";
        private boolean mock = true;
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public boolean isMock() { return mock; }
        public void setMock(boolean mock) { this.mock = mock; }
    }

    public static class Upload {
        private long maxFileSize = 2147483648L;
        public long getMaxFileSize() { return maxFileSize; }
        public void setMaxFileSize(long maxFileSize) { this.maxFileSize = maxFileSize; }
    }
}
