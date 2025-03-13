package com.niitr_api.niitr_api.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Map;

@Component
public class AESEncryption {

    public String encrypt(Map<String, Object> params) {
        try {
            String payload = new ObjectMapper().writeValueAsString(params);
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            KeySpec spec = new PBEKeySpec(GlobalValue.SECRET_KEY.toCharArray(), GlobalValue.SALT.getBytes(StandardCharsets.UTF_8), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] encryptedText = cipher.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            byte[] encryptedWithIV = new byte[iv.length + encryptedText.length];
            System.arraycopy(iv, 0, encryptedWithIV, 0, iv.length);
            System.arraycopy(encryptedText, 0, encryptedWithIV, iv.length, encryptedText.length);

            return Base64.getEncoder().encodeToString(encryptedWithIV);

        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle encryption failure gracefully
        }
    }

  
}
