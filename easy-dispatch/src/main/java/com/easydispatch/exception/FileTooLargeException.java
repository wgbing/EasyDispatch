package com.easydispatch.exception;

public class FileTooLargeException extends BusinessException {
    public FileTooLargeException(long size, long limit) {
        super(413, "文件过大，单文件限制 " + humanSize(limit) + "，当前 " + humanSize(size));
    }

    private static String humanSize(long bytes) {
        if (bytes >= 1024L * 1024 * 1024) return (bytes / 1024 / 1024 / 1024) + "GB";
        if (bytes >= 1024L * 1024) return (bytes / 1024 / 1024) + "MB";
        if (bytes >= 1024L) return (bytes / 1024) + "KB";
        return bytes + "B";
    }
}
