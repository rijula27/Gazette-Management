package spring.aop.gazettemanagementnic.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class AesUtil {

    private final byte[] keyBytes;

    public AesUtil(@Value("${aes.secret}") String aesSecret) {
        try {
            // derive 32-byte key using SHA-256 of aesSecret
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = sha.digest(aesSecret.getBytes(StandardCharsets.UTF_8));
            this.keyBytes = key; // 32 bytes
        } catch (Exception e) {
            throw new RuntimeException("Failed to init AesUtil", e);
        }
    }

    /**
     * Decrypts payload formatted as "ivBase64:cipherBase64"
     */
    public String decrypt(String payload) {
        try {
            if (payload == null || !payload.contains(":")) {
                throw new IllegalArgumentException("Invalid payload format");
            }
            String[] parts = payload.split(":", 2);
            String ivB64 = parts[0];
            String cipherB64 = parts[1];

            byte[] iv = Base64.getDecoder().decode(ivB64);
            byte[] cipherBytes = Base64.getDecoder().decode(cipherB64);

            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

            byte[] plainBytes = cipher.doFinal(cipherBytes);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new RuntimeException("AES decryption failed", ex);
        }
    }
}
