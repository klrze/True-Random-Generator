/* Description:	Starts up Spring Boot app and initializes components
				Example command in terminal: java -Dvault.passkey=PASSKEY -jar target/trng-0.0.1-SNAPSHOT.jar
				Requires the correct passkey to proceed
*/
package com.example.trng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication; // Marks class as main Spring Boot entry point
import org.springframework.boot.CommandLineRunner; // Allows CLI interaction and controls
import org.springframework.beans.factory.annotation.Autowired; // Allows instances to be injected
import java.util.Scanner; // Allows user input from console to be read

@SpringBootApplication // Marks class as main Spring Boot entry point
public class TrngApplication implements CommandLineRunner {
	// Creates instances of other classes to be used in this file
	@Autowired private SerialListener serialListener;
	@Autowired private EncryptionService encryptionService;
	@Autowired private VaultReader vaultReader;

	public static void main(String[] args) {
		SpringApplication.run(TrngApplication.class, args);
	}

	@Override // Implementing method from CLI interface
	public void run(String... args) throws Exception {
		// Retrieves passkey from "-D" indicator and stores in variable
		String passkey = System.getProperty("vault.passkey");
		// Deny access if passkey is not provided
		if (passkey == null) {
			System.err.println("❌ Access Denied: Run with -Dvault.passkey=[key]");
			System.exit(0);
		}
		// Calls EncryptionService and passes "passkey" variable to "init" method
		encryptionService.init(passkey);
		// Creates new Scanner to read 
		Scanner uiScanner = new Scanner(System.in);
		// Prints options onto console
		System.out.println("\n--- 🛡️ CRYPTO-VAULT TERMINAL ---");
		System.out.println("1. [RECORD] Listen to Hardware");
		System.out.println("2. [RECOVER] View Decrypted History");
		System.out.print("Selection: ");
		// Stores user's input into variable
		String choice = uiScanner.nextLine();
		// If user chooses "1"
		if (choice.equals("1")) {
			// Calls "start" method in SerialListener class and passes port name
			serialListener.start("/dev/cu.usbserial-DN051B1M");
			System.out.println("📡 Blind recording active. Press button on Arduino...");
			while(true) { Thread.sleep(1000); }
		}
		// If user chooses "2"
		else if (choice.equals("2")) {
			// Calls "showMeTheNumbers" method in VaultReader
			vaultReader.showMeTheNumbers();
		}
	}
}
