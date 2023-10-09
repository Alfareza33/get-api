package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;

@RestController
@RequestMapping("/api")
public class JobController {
    private final String API_BASE_URL = "https://dev6.dansmultipro.com/api/recruitment/";

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/pekerjaan/daftar")
    public ResponseEntity<String> getDaftarPekerjaan() {
        String url = API_BASE_URL + "positions.json";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/pekerjaan/detail/{id}")
    public ResponseEntity<String> getDetailPekerjaan(@PathVariable UUID id) {
        String url = API_BASE_URL + "positions/" + id;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/pekerjaan/daftar/csv")
    public ResponseEntity<String> unduhDaftarPekerjaanCsv() {
        String url = "http://dev6.dansmultipro.co.id/api/recruitment/positions.json";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Implementasikan konversi dari JSON ke CSV
        String csvData = convertJsonToCsv(response.getBody());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set("Content-Disposition", "attachment; filename=daftar_pekerjaan.csv");

        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }

    // Fungsi untuk mengonversi JSON ke CSV
    private String convertJsonToCsv(String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode arrayNode = objectMapper.readValue(jsonData, ArrayNode.class);

            StringBuilder csvData = new StringBuilder();

            // Header CSV (mengambil nama properti dari objek pertama)
            ObjectNode firstObject = (ObjectNode) arrayNode.get(0);
            Iterator<String> fieldNames = firstObject.fieldNames();
            while (fieldNames.hasNext()) {
                csvData.append(fieldNames.next()).append(",");
            }
            csvData.append("\n");

            // Data CSV
            for (int i = 0; i < arrayNode.size(); i++) {
                ObjectNode objectNode = (ObjectNode) arrayNode.get(i);
                fieldNames = objectNode.fieldNames();
                while (fieldNames.hasNext()) {
                    csvData.append(objectNode.get(fieldNames.next()).asText()).append(",");
                }
                csvData.append("\n");
            }

            return csvData.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
