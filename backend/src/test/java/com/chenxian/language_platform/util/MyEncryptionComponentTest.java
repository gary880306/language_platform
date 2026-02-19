package com.chenxian.language_platform.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyEncryptionComponentTest {

    @Autowired
    private MyEncryptionComponent myEncryptionComponent;

    @Test
    public void testEncryptionDecryption() throws Exception {
        String originalMessage = "zaq123456789";
        System.out.println("Original Message: " + originalMessage);

        String encryptedMessage = myEncryptionComponent.encrypt(originalMessage);
        System.out.println("Encrypted Message: " + encryptedMessage);

        String decryptedMessage = myEncryptionComponent.decrypt(encryptedMessage);
        System.out.println("Decrypted Message: " + decryptedMessage);

        // 添加断言来验证加解密是否成功
        // Assert.assertEquals(originalMessage, decryptedMessage);
    }
}
