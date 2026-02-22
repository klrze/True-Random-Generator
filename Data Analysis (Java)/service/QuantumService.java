package service;

import java.net.URI; // Manages URL processing
import java.net.http.HttpClient; // Manages communication between app and API servers
import java.net.http.HttpRequest; // To send requests to API server
import java.net.http.HttpResponse; // Receive responses from API server
import org.json.JSONObject; // Object being transported from API server
import org.json.JSONArray; // Represents a collection of objects
import java.time.Duration; 
import java.security.SecureRandom;

public class QuantumService {

    // API URL
    private static final String API_URL = "https://api.qrng.outshift.com/api/v1/random_numbers";
    // API Passkey
    private static final String API_KEY = "3}7-p<)ynMe[350t2PWa239sQtB)9FtbMgS3:pZ;D-z!5(6d@W8{GTT<kF)7xA6+";

    // Static method: Can be called w/o creating new instance
    // Returns a string
    public static String getQuantumNumber() { 
        try {
            HttpClient client = HttpClient.newBuilder() // Creates browser
                    .connectTimeout(Duration.ofSeconds(5)) // Give up if can't find server in 5 seconds
                    .build(); // Finalizes browser creation

            // Cisco API requirements for a single 16-bit decimal number
            String jsonBody = "{\"encoding\": \"raw\", \"format\": \"decimal\", \"bits_per_block\": 16, \"number_of_blocks\": 1}";

            // Starts creating request to be sent to API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL)) // Points to specific server to talk to
                    .header("x-id-api-key", API_KEY) // Signals for API key
                    .header("Content-Type", "application/json") // Signals that JSON data is being sent to API
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody)) // Signals intent to save data after sending
                    .build();
            
            // Stores API reply in "response"
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) { // Code for success
                // Converts data from API into Java object
                JSONObject json = new JSONObject(response.body());

                // MATCHING THE NESTED STRUCTURE: random_numbers -> Array[0] -> decimal
                if (json.has("random_numbers")) { 
                    // Find label "random_numbers" and treat as a list
                    JSONArray arr = json.getJSONArray("random_numbers");
                    if (arr.length() > 0) {
                        // Takes first item in list
                        JSONObject firstResult = arr.getJSONObject(0);
                        if (firstResult.has("decimal")) { 
                            String val = firstResult.getString("decimal");
                            System.out.println("✅ Quantum Sync Success (Cisco): " + val);
                            return val;
                        }
                    }
                }
                System.out.println("❓ API responded, but JSON structure was unexpected.");
            } else {
                System.out.println("❌ Cisco API Error (Status " + response.statusCode() + ")");
            }
        } catch (Exception e) {
            System.err.println("📡 Connection Error: " + e.getMessage());
        }

        return getLocalFallback();
    }

    private static String getLocalFallback() {
        System.out.println("⚠️ Using SecureLocal (Hardware Entropy) Fallback...");
        SecureRandom sr = new SecureRandom();
        // Generate a 0-65535 number to mimic the uint16 API response
        return String.valueOf(sr.nextInt(65536));
    }
}
