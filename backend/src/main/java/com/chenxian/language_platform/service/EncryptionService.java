package com.chenxian.language_platform.service;

/**
 * 加密服務介面 (DIP: 高層模組依賴抽象，而非具體實作)
 */
public interface EncryptionService {
    String encrypt(String message) throws Exception;
    String decrypt(String encryptedMessage) throws Exception;
}
