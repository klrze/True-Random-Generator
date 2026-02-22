/* Description: Entry point of program
                Bridge for communication from Arduino Uno via serial ports
                Fetches data from API
                Creates entity objects with corresponding data
                Stores data in database
*/
?
package main;

import entity.RawData; // Access to RawData entity file in "entity" package
import repository.RawDataRepository; // Access to RawDataRepository file in "repository" package
import service.QuantumService; // Access to QuantumService file in "service" package
import com.fazecast.jSerialComm.SerialPort; // Allows communication from physical hardware
import org.springframework.beans.factory.annotation.Autowired; // Dependency injection
import org.springframework.boot.CommandLineRunner; // Automatically starts up program in console
import org.springframework.boot.SpringApplication; // Launches Spring Boot application
import org.springframework.boot.autoconfigure.SpringBootApplication; // Configurates application
import org.springframework.boot.autoconfigure.domain.EntityScan; // Allows entities from other packages to be utilized
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; // Allows repository package to be utilized

import java.io.BufferedReader; // Waits to send data until a new line of data is signaled
import java.io.InputStreamReader; // Translates raw bytes into characters

@SpringBootApplication // Auto-configs all need components
@EntityScan("entity") // Scans for existing entities to utilize
@EnableJpaRepositories("repository") // Scans for existing repositories to utilize
public class Arduino implements CommandLineRunner {

    // Allows methods in RawDataRepository to be utilized
    @Autowired
    private RawDataRepository rawDataRepository;

    // Entry point to initialize program
    public static void main(String[] args) {
        SpringApplication.run(Arduino.class, args);
    }

    @Override // Indicates to run this specific logic immediately
    public void run(String... args) throws Exception {
        String myPortName = "/dev/cu.usbserial-DN051B1M"; // Serial port name
        SerialPort comPort = SerialPort.getCommPort(myPortName); // Establishes connection with indicated port
        comPort.setBaudRate(9600);

        if (comPort.openPort()) { // Successfully connected to pot
            System.out.println("🚀 System Online. Waiting for Arduino data on " + myPortName);
        } else { // Unsuccessfully connected to port
            System.err.println("❌ Fatal Error: Could not open Serial Port.");
            return;
        }

        // Receives and translates raw bytes into characters
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(comPort.getInputStream()))) {
            while (true) {
                if (reader.ready()) { // Checks if data is available and ready to be read
                    String line = reader.readLine();
                    if (line != null && line.contains("DATA,")) { // Indicates to only process certain data and not all
                        processAndSave(line);
                    }
                }
                Thread.sleep(50); // Gives CPU a break
            }
        } finally {
            comPort.closePort();
        }
    }

    private void processAndSave(String line) {
        try {
            // Clean and split the Arduino string
            String dataPart = line.substring(line.indexOf("DATA,")); // Ignores everything until signal word is found
            String[] parts = dataPart.split(","); // Utilizes comma as data splitter indicator

            if (parts.length >= 3) { // Checks if there are three slots of data occupied
                // Parses data into a Long and trims unnecessary spaces
                long trng = Long.parseLong(parts[1].trim()); 
                long prng = Long.parseLong(parts[2].trim());

                // Fetch QRNG via getQuantumNumber method in imported QuantumService class
                String rawResult = QuantumService.getQuantumNumber();

                // Ensures QRNG doesn't exceed 1000 and stays within range
                long qrngFinal = Long.parseLong(rawResult) % 1001;

                // Create Entity
                RawData data = new RawData();
                data.setTrngNum(trng);
                data.setPrngNum(prng);
                data.setQrngNum(qrngFinal);
                
                // Saves to database
                RawData saved = rawDataRepository.save(data);

                // Confirmation output
                System.out.println("================================");
                System.out.println("💾 SAVED TO SUPABASE | TRIAL #" + saved.getTrialNum());
                System.out.println("Values: TRNG=" + trng + " PRNG=" + prng + " QRNG=" + qrngFinal);
                System.out.println("================================");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Trial Processing Failed: " + e.getMessage());
        }
    }
}
