/* Description: Performs encryption and decryption on data */

package com.example.trng;

import org.springframework.stereotype.Service;
import javax.crypto.Cipher; // Engine for standard encryption algorithms
import javax.crypto.spec.SecretKeySpec; // Formatter for Cipher
import java.nio.charset.StandardCharsets; // Hashes data to meet requirements of Cipher
import java.security.MessageDigest; // Ensures final key is perfect size
import java.util.Base64; // Translates bytes into clean string

@Service // Indicates class as service class
public class EncryptionService {
    private SecretKeySpec secretKey;

    // Hashing passkey into non-traceable byte, and derives/ seed from passkey to initiate AES
    public void init(String passkey) throws Exception {
        // Prompts to use SHA-256 
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        // Turns passkey into numerical-equivalent and performs hashing
        byte[] keyBytes = sha.digest(passkey.getBytes(StandardCharsets.UTF_8));
        // Passes the required passkey that is needed to initiate AES
        // keyBytes is the "seed" of the AES
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    // Encryption occurs to turn raw data into ciphertext via AES initialized by passkey acting as a seed
    public String encrypt(String rawData) throws Exception {
        // Creates an object to prompt to use AES
        Cipher cipher = Cipher.getInstance("AES");
        // Explicitly indicating that Cipher is encrypting
        // Prompting to use the passkey as the seed to encrypt from
        cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        // Prompts to start AES encryption
        byte[] encryptedBytes = cipher.doFinal(rawData.getBytes(StandardCharsets.UTF_8));
        // Translates encrypted data into specific alphabet and symbols
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Performs decryption by reverse encoding back to bytes then decrypting using seed (passkey)
    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        // Explicitly indicating that Cipher is decrypting
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        // Reverses encoder by translating back to bytes
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        // Decrypts bytes by using seed (passkey) to reverse process
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
