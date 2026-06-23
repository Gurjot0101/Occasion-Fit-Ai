package com.occasionfit.backend.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
@Log4j2
public class PollinationsClient {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${pollinations.api-key}")
    private String pollinationsApiKey;

    @Value("${pollinations.image.url}")
    private String pollinationsImageUrl;

    @Value("${pollinations.upload.url}")
    private String pollinationsUploadUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String generateOutfitImage(String userPrompt) throws Exception {
        try {
            String fullPrompt = buildImagePrompt(userPrompt);
            String encodedPrompt = URLEncoder.encode(fullPrompt, StandardCharsets.UTF_8);
            long seed = Math.abs(fullPrompt.hashCode());

            String genUrl = pollinationsImageUrl + encodedPrompt
                    + "?model=flux&nologo=true&seed=" + seed;

            // Step 1: generate — key stays in the header, never in a URL
            HttpRequest genRequest = HttpRequest.newBuilder()
                    .uri(URI.create(genUrl))
                    .header("Authorization", "Bearer " + pollinationsApiKey.trim())
                    .GET()
                    .build();

            HttpResponse<byte[]> genResponse = httpClient.send(genRequest, HttpResponse.BodyHandlers.ofByteArray());

            if (genResponse.statusCode() != 200) {
                log.error("Pollinations image gen failed: status={}", genResponse.statusCode());
                return null;
            }

            byte[] imageBytes = genResponse.body();

            // Step 2: upload bytes → get back a public, key-free URL
            String boundary = "----OccasionFitBoundary" + System.currentTimeMillis();
            byte[] multipartBody = buildMultipartBody(boundary, imageBytes);

            HttpRequest uploadRequest = HttpRequest.newBuilder()
                    .uri(URI.create(pollinationsUploadUrl))
                    .header("Authorization", "Bearer " + pollinationsApiKey.trim())
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                    .build();

            HttpResponse<String> uploadResponse = httpClient.send(uploadRequest, HttpResponse.BodyHandlers.ofString());

            if (uploadResponse.statusCode() != 200) {
                log.error("Pollinations media upload failed: status={}, body={}",
                        uploadResponse.statusCode(), uploadResponse.body());
                return null;
            }

            JsonNode root = objectMapper.readTree(uploadResponse.body());

            return root.get("url").asText();

        } catch (Exception e) {
            log.error("Pollinations Flux generate+upload error: {}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    private String buildImagePrompt(String userPrompt) {
        return "Generate a fashion outfit image: " + userPrompt + ". Professional fashion photography.";
    }

    private byte[] buildMultipartBody(String boundary, byte[] imageBytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String header = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"outfit.jpg\"\r\n"
                + "Content-Type: image/jpeg\r\n\r\n";
        out.write(header.getBytes(StandardCharsets.UTF_8));
        out.write(imageBytes);
        String footer = "\r\n--" + boundary + "--\r\n";
        out.write(footer.getBytes(StandardCharsets.UTF_8));
        return out.toByteArray();
    }
}
