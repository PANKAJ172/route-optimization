package com.route.service;

import com.route.model.Stop;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {
    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadCsvFileToS3(String vehicleId, File csvFile) throws IOException {
        String key = vehicleId + ".csv";
        byte[] fileBytes = Files.readAllBytes(csvFile.toPath());
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/csv")
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));
        System.out.println("CSV file uploaded to S3 successfully with key: " + key);
    }

    public List<Stop> getStopsFromCsv(String vehicleId) throws IOException {
        String key = vehicleId + ".csv";
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        ResponseInputStream<GetObjectResponse> s3ObjectInputStream = s3Client.getObject(getObjectRequest);
        return parseCsv(s3ObjectInputStream);
    }

    public List<Stop> parseCsv(InputStream inputStream) {
        List<Stop> stops = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                Stop stop = new Stop(
                        values[0],
                        Integer.parseInt(values[1]),
                        Integer.parseInt(values[2])
                );
                stops.add(stop);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stops;
    }

    public File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
