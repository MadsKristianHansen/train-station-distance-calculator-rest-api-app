package com.db.trainstationdistancecalculatorrestapiapp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;

import java.util.Optional;

// create a RestController in order to process HTTP-Requests
@RestController
public class CalculatorController {

    // Dependency Injection: automatically connect CRUDRepository with @Autowired Annotation
    @Autowired
    private StationDataRepository stationDataRepository;

    // 1.Mockup-Test:
    @GetMapping("/api/v1/distance/FF/BLS/Test")
    public ResponseEntity<DistanceData> getMockupDistanceData(){

        DistanceData finalDistanceData = new DistanceData();
        finalDistanceData.setFrom("Frankfurt(Main)Hbf");
        finalDistanceData.setTo("Berlin Hbf");
        finalDistanceData.setDistance(423);
        finalDistanceData.setUnit("km");

        return new ResponseEntity<DistanceData>(finalDistanceData, HttpStatus.OK);

    }

    // Test: get <StationData> via id/primary key from db
    @GetMapping("/api/stationData")
    public ResponseEntity<StationData> getStationData(@RequestParam(value = "id") int id){

        Optional<StationData> stationDataInDb = stationDataRepository.findById(id);

        if(stationDataInDb.isPresent()){
            return new ResponseEntity<StationData>(stationDataInDb.get(), HttpStatus.OK);
        }

        return new ResponseEntity("No stationData found with id " + id,HttpStatus.NOT_FOUND);

    }

    // Method to calculate the air-line distance in km between two points with given latitudes and longitudes
    public static double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        int radius = 6371;

        double lat = Math.toRadians(lat2 - lat1);
        double lon = Math.toRadians(lon2- lon1);

        double a = Math.sin(lat / 2) * Math.sin(lat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lon / 2) * Math.sin(lon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = radius * c;

        return Math.abs(d);
    }

    // Method to get the DistanceData between two trainStations via @RequestParam
    @GetMapping("/api/v1/distance")
    public ResponseEntity<String> getDistanceData(@RequestParam(value = "ds100") String ds100,
                                                      @RequestParam(value = "ds100_2") String ds100_2){

        // search for StationData with given ds100-codes in DB
        var validStationDataInDb = stationDataRepository.findByDs100(ds100);
        var validStationDataInDb2 = stationDataRepository.findByDs100(ds100_2);

        // execute further code if StationData has been found
        if(validStationDataInDb.isPresent() && validStationDataInDb2.isPresent()) {

            // get needed values from StationData and set them to variables
            String stationDataFrom = validStationDataInDb.get().getName();
            String stationDataTo = validStationDataInDb2.get().getName();
            Double stationData1Laenge = validStationDataInDb.get().getLaenge();
            Double stationData1Breite = validStationDataInDb.get().getBreite();
            Double stationData2Laenge = validStationDataInDb2.get().getLaenge();
            Double stationData2Breite = validStationDataInDb2.get().getBreite();

            // calculate air-line distance between two stations with given values
            Double distance = distanceInKm(stationData1Breite, stationData1Laenge, stationData2Breite, stationData2Laenge);
            // round solution to full integer
            int distanceRound = (int) Math.round(distance);

            // create new JSONObject with desired keys and values
            JSONObject distanceData = new JSONObject();
            distanceData.put("from", stationDataFrom);
            distanceData.put("to", stationDataTo);
            distanceData.put("distance", distanceRound);
            distanceData.put("unit", "km");

            // return final JSONObject
            return new ResponseEntity<String>(distanceData.toString(), HttpStatus.OK);
        }

        // return NOT_FOUND Message if one or both ds100-codes are incorrect/not stored in DB
        return new ResponseEntity("One or more stationData not found with ds100 " + ds100 + " and/or ds100 " + ds100_2,HttpStatus.NOT_FOUND);

    }

    // Method to get the DistanceData between two trainStations via @PathVariable
    @GetMapping("/api/v1/distance/{ds100}/{ds100_2}")
    public ResponseEntity<DistanceData> getDistanceDataByPath(@PathVariable(value = "ds100") String ds100,
                                                  @PathVariable(value = "ds100_2") String ds100_2){

        // search for StationData with given ds100-codes in DB
        var validStationDataInDb = stationDataRepository.findByDs100(ds100);
        var validStationDataInDb2 = stationDataRepository.findByDs100(ds100_2);

        // execute further code if StationData has been found
        if(validStationDataInDb.isPresent() && validStationDataInDb2.isPresent()) {

            // get needed values from StationData and set them to variables
            String stationDataFrom = validStationDataInDb.get().getName();
            String stationDataTo = validStationDataInDb2.get().getName();
            Double stationData1Laenge = validStationDataInDb.get().getLaenge();
            Double stationData1Breite = validStationDataInDb.get().getBreite();
            Double stationData2Laenge = validStationDataInDb2.get().getLaenge();
            Double stationData2Breite = validStationDataInDb2.get().getBreite();

            // calculate air-line distance between two stations with given values
            Double distance = distanceInKm(stationData1Breite, stationData1Laenge, stationData2Breite, stationData2Laenge);
            // round solution to full integer
            int distanceRound = (int) Math.round(distance);

            // create new DistanceDataObject with desired values
            DistanceData finalDistanceData = new DistanceData();
            finalDistanceData.setFrom(stationDataFrom);
            finalDistanceData.setTo(stationDataTo);
            finalDistanceData.setDistance(distanceRound);
            finalDistanceData.setUnit("km");

            // return final DistanceData
            return new ResponseEntity<DistanceData>(finalDistanceData, HttpStatus.OK);
        }

        // return NOT_FOUND Message if one or both ds100-codes are incorrect/not stored in DB
        return new ResponseEntity("One or more stationData not found with ds100 " + ds100 + " and/or ds100 " + ds100_2,HttpStatus.NOT_FOUND);

    }

    // Method to post and store <StationData> in DB
    @PostMapping("/api/stationData")
    public ResponseEntity<StationData> createStationData(@RequestBody StationData newStationData){
        stationDataRepository.save(newStationData);
        return new ResponseEntity<StationData>(newStationData, HttpStatus.OK);
    }

}
