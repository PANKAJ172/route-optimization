# Route Optimization

## Overview
The Logistics System is a robust solution designed to optimize delivery routes for vehicles, enabling efficient logistics operations. The system implements a Traveling Salesman Problem (TSP) algorithm to determine the shortest route that visits a set of designated stops, ensuring minimal travel distance.

## Key Features
- **Dynamic Route Calculation**: Utilizes the nearest neighbor algorithm for quick route optimization.
- **CSV Integration**: Reads stop coordinates from CSV files stored in AWS S3 for flexible data management.
- **Distance Calculation**: Computes the total travel distance, providing insights for operational efficiency.
- **Chart Visualization**: Generates graphical representations of routes using XChart.

## Technologies Used
- Java Spring Boot
- AWS SDK for S3
- Apache Commons CSV (or OpenCSV for parsing CSV files)
- XChart for chart visualization

## Getting Started
1. Clone the repository.
2. Configure AWS S3 credentials.
3. Run the application and upload CSV files for route optimization.

