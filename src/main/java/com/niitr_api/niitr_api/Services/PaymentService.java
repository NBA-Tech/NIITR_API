package com.niitr_api.niitr_api.Services;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.niitr_api.niitr_api.Utils.GlobalValue;

@Component
public class PaymentService {

     public String paymentPostRequest(String encryptedPayload) throws Exception {
        URL url = new URL(GlobalValue.AUTH_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String requestBody = "{ \"merchId\": \"" + GlobalValue.MERCHANT_ID + "\", \"encData\": \"" + encryptedPayload + "\" }";
        System.out.println("payload"+requestBody);


        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    public String paymentGetRequest(String encryptedPayload) throws Exception {
        URL url = new URL(GlobalValue.AUTH_URL + "?merchId=" + GlobalValue.MERCHANT_ID + "&encData=" + encryptedPayload);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }
    
}
