package com.route.controller;

import com.route.model.Stop;
import com.route.service.RoutePlotterService;
import com.route.service.S3Service;
import com.route.utils.Calculations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private Calculations calculations;
    @Autowired
    private RoutePlotterService routePlotterService;

    @Value("${xchart.plotter.filePath}")
    private String filePath;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("vehId") String vehId, @RequestParam("file") MultipartFile file) {
        try {
            File csvFile = s3Service.convertMultiPartToFile(file);
            s3Service.uploadCsvFileToS3(vehId, csvFile);
            csvFile.delete();
            return ResponseEntity.ok("File uploaded successfully for vehicleId: " + vehId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<String> optimizeRoute(@PathVariable String vehicleId) throws IOException {
        List<Stop> stops = s3Service.getStopsFromCsv(vehicleId);
        List<Stop> optimizedStops = calculations.optimizeRoute(stops);
        double totalDistance = calculations.calculateTotalDistance(optimizedStops);
        routePlotterService.generateRouteChart(optimizedStops, totalDistance, filePath);
        BigDecimal roundedValue = new BigDecimal(totalDistance).setScale(2, RoundingMode.HALF_UP);
        StringBuilder result = new StringBuilder("Total distance : " + roundedValue + " \nRoute : ");
        for (Stop stop : optimizedStops) {
            result.append(stop.getStopId()).append(",");
        }
        return ResponseEntity.ok(result.toString());
    }
}
