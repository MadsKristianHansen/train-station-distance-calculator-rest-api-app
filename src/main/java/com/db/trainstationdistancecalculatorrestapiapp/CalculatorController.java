package com.db.trainstationdistancecalculatorrestapiapp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;

import java.util.Optional;


@RestController
public class CalculatorController {

    @Autowired
    private StationDataRepository stationDataRepository;

    @GetMapping("/api/v1/distance/FF/BLS")
    public ResponseEntity<DistanceData> getMockupDistanceData(){

        DistanceData finalDistanceData = new DistanceData();
        finalDistanceData.setFrom("Frankfurt(Main)Hbf");
        finalDistanceData.setTo("Berlin Hbf");
        finalDistanceData.setDistance(423);
        finalDistanceData.setUnit("km");

        return new ResponseEntity<DistanceData>(finalDistanceData, HttpStatus.OK);

    }

    @GetMapping("/api/stationData")
    public ResponseEntity<StationData> getStationData(@RequestParam(value = "id") int id){

        Optional<StationData> stationDataInDb = stationDataRepository.findById(id);

        if(stationDataInDb.isPresent()){
            return new ResponseEntity<StationData>(stationDataInDb.get(), HttpStatus.OK);
        }

        return new ResponseEntity("No stationData found with id " + id,HttpStatus.NOT_FOUND);

    }

    public static double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        int radius = 6371;

        double lat = Math.toRadians(lat2 - lat1);
        double lon = Math.toRadians(lon2- lon1);

        double a = Math.sin(lat / 2) * Math.sin(lat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lon / 2) * Math.sin(lon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = radius * c;

        return Math.abs(d);
    }

    @GetMapping("/api/vi/distance")
    public ResponseEntity<String> getDistanceData(@RequestParam(value = "ds100") String ds100,
                                                      @RequestParam(value = "ds100_2") String ds100_2){

        var validStationDataInDb = stationDataRepository.findByDs100(ds100);
        var validStationDataInDb2 = stationDataRepository.findByDs100(ds100_2);

        if(validStationDataInDb.isPresent() && validStationDataInDb2.isPresent()) {

            String stationDataFrom = validStationDataInDb.get().getName();
            String stationDataTo = validStationDataInDb2.get().getName();
            Double stationData1Laenge = validStationDataInDb.get().getLaenge();
            Double stationData1Breite = validStationDataInDb.get().getBreite();
            Double stationData2Laenge = validStationDataInDb2.get().getLaenge();
            Double stationData2Breite = validStationDataInDb2.get().getBreite();

            Double distance = distanceInKm(stationData1Breite, stationData1Laenge, stationData2Breite, stationData2Laenge);
            int distanceRound = (int) Math.round(distance);

            JSONObject distanceData = new JSONObject();
            distanceData.put("from", stationDataFrom);
            distanceData.put("to", stationDataTo);
            distanceData.put("distance", distanceRound);
            distanceData.put("unit", "km");

            return new ResponseEntity<String>(distanceData.toString(), HttpStatus.OK);
        }

        return new ResponseEntity("One or more stationData not found with ds100 " + ds100 + " and/or ds100 " + ds100_2,HttpStatus.NOT_FOUND);

    }

    @PostMapping("/api/stationData")
    public ResponseEntity<StationData> createStationData(@RequestBody StationData newStationData){
        stationDataRepository.save(newStationData);
        return new ResponseEntity<StationData>(newStationData, HttpStatus.OK);
    }

}
