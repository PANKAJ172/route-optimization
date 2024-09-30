package com.route.service;

import com.route.model.Stop;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoutePlotterService {

    public void generateRouteChart(List<Stop> route, double totalDistance, String filePath) throws IOException {
        List<Double> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();

        for (Stop stop : route) {
            xData.add((double) stop.getX());
            yData.add((double) stop.getY());
        }

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Optimized Route - Total Distance: " + String.format("%.2f", totalDistance) + " units")
                .xAxisTitle("X Coordinate")
                .yAxisTitle("Y Coordinate")
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
        chart.getStyler().setLegendLayout(Styler.LegendLayout.Vertical);
        chart.getStyler().setMarkerSize(15);

        XYSeries series = chart.addSeries("Route", xData, yData);
        series.setMarker(SeriesMarkers.TRIANGLE_UP);
        series.setMarkerColor(Color.BLUE);

        series.setLineStyle(SeriesLines.SOLID);
        series.setLineColor(Color.green);

        for (int i = 0; i < route.size(); i++) {
            Stop stop = route.get(i);
            chart.addAnnotation(new AnnotationText("Stop " + stop.getStopId(), stop.getX(), stop.getY(), true));
        }


        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        // Save the chart as a PNG file
        BitmapEncoder.saveBitmap(chart, new FileOutputStream(file), BitmapEncoder.BitmapFormat.PNG);
    }
}
