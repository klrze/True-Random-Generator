/* Description: Conducts decryption when correct passkey is inserted
*/

package com.example.trng;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Indicates that this is a service class
public class VaultReader {
    // Creates instances and injects
    @Autowired private VaultRepository repository;
    @Autowired private EncryptionService encryptionService;

    public void showMeTheNumbers() {
        System.out.println("\n📂 --- OPENING THE SECURE VAULT ---");
        // Each row in database becomes a List of SecureEntry objects
        List<SecureEntry> entries = repository.findAll();

        for (SecureEntry entry : entries) {
            try {
                // Takes encrypted data and passes it to "decrypt" method in EncryptionService
                String decrypted = encryptionService.decrypt(entry.getEncryptedPayload());
                System.out.println("🆔 ID: " + entry.getId() + " | 🔢 Original Number: " + decrypted);
            } catch (Exception e) {
                // Denies if passkey is wrong
                System.out.println("🆔 ID: " + entry.getId() + " | ❌ [DECRYPTION FAILED - WRONG KEY]");
            }
        }
        System.out.println("-----------------------------------\n");
    }
}
