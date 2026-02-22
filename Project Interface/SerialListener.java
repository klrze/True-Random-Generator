/* Description: "Bridge" between Arduino Uno and Intellij 
                 Receives the data from Arduino
*/

package com.example.trng;

import com.fazecast.jSerialComm.SerialPort; // Allows communication to the USB ports w/ SerialPort object
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Scanner;

@Service // Indicates that this class is a service class
public class SerialListener {
    @Autowired private VaultRepository repository;
    @Autowired private EncryptionService encryptionService;

    public void start(String portName) {
        // Receives port name and stores in "comPort" variable
        SerialPort comPort = SerialPort.getCommPort(portName);
        // Speed that data transfers at (Arduino & Java must have same rate)
        comPort.setBaudRate(115200);
        // Physically opens port
        if (comPort.openPort()) {
            System.out.println("✅ Hardware Link Established.");
            /* Code waits until data arrives
               Stays active in background while other components of app is responsive */             
            new Thread(() -> {
                // Reads the stream of bytes coming through the USB wire
                try (Scanner scanner = new Scanner(comPort.getInputStream())) {
                    // Loop runs forever and waits for Arduino to send text (data)
                    while (scanner.hasNextLine()) {
                        // Waits and reads until next line is hit, then trims extra spaces
                        String line = scanner.nextLine().trim();
                        // Ignores data sent that isn't marked with indicating phrase below
                        if (line.startsWith("SECURE_TRNG:")) {
                            // Splits the text (data) from Arduino at the colon and takes the remainder
                            String data = line.split(":")[1].trim();
                            // Takes data and starts storage process
                            // (method below)
                            saveBlindly(data);
                        }
                    }
                }
            }).start(); // Starts the thread
        }
    }

    private void saveBlindly(String rawData) {
        try {
            // Calls encrypt method in EncryptionService and passes the data to it
            String encrypted = encryptionService.encrypt(rawData);
            // Creates new database object
            SecureEntry entry = new SecureEntry();
            // Passes encrypted data to SecureEntry and calls method
            entry.setEncryptedPayload(encrypted);
            // JPA command that writes to SQL database 
            repository.save(entry);
            System.out.println("🔒 [VAULTED] Ciphertext: " + encrypted);
        } catch (Exception e) { System.err.println("Error: " + e.getMessage()); }
    }
}
