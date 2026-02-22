/* Description: Blueprint for database
                Formatted to turn Java objects into SQL rows 
*/

package com.example.trng;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity // Indicates that this class represents a table in a database
@Table(name = "secure_trng_vault") // Customized name for table
public class SecureEntry {
    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String encryptedPayload;

    private LocalDateTime generatedAt = LocalDateTime.now();

    // Getters
    public Long getId() { return id; }
    public String getEncryptedPayload() { return encryptedPayload; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    // Setter
    public void setEncryptedPayload(String payload) { this.encryptedPayload = payload; }
}
