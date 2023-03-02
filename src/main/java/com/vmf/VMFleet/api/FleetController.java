package com.vmf.VMFleet.api;

import com.vmf.VMFleet.dao.VehicleData;
import com.vmf.VMFleet.api.model.VehicleRegisterRequest;
import com.vmf.VMFleet.dao.VfmMetrics;
import com.vmf.VMFleet.exceptions.VehicleAlreadyExistsException;
import com.vmf.VMFleet.exceptions.VehicleInActiveException;
import com.vmf.VMFleet.exceptions.VehicleNotFoundException;
import com.vmf.VMFleet.service.ReportService;
import com.vmf.VMFleet.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/vmf")
@Slf4j
public class FleetController {

    @Autowired
    VehicleService vehicleService;

    @Autowired
    ReportService reportService;

    @PostMapping("/v1/vehicle")
    public ResponseEntity addVehicleToFleet(@RequestBody VehicleRegisterRequest vehicleRegistrationRequest) {
        try {
            log.info("Received new vehicle request");
            return new ResponseEntity(vehicleService.addVehicle(vehicleRegistrationRequest), HttpStatus.CREATED);
        } catch(VehicleAlreadyExistsException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/v1/vehicle")
    public ResponseEntity getAllVehicles() {
        log.info("Received query vehicle request");
        return new ResponseEntity<>(vehicleService.getAllVehicle(), HttpStatus.OK);
    }

    @PostMapping("/v1/vehicle/{id}/metric")
    public ResponseEntity pushMetric(@PathVariable int id, @RequestBody VehicleData metrics) {
        try {
            vehicleService.pushNewMetric(metrics);
            log.info(String.format("Metrics published for vehicle with id %s", metrics.getVehicleId()));
        } catch (VehicleNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (VehicleInActiveException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (KafkaException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/v1/vehicle/{id}/metric")
    public ResponseEntity getMetric(@PathVariable int id) {
        try {
           List<VfmMetrics> metricsList =  vehicleService.getVehicleMetricsById(id);
           return new ResponseEntity(metricsList, HttpStatus.OK);
        } catch (VehicleNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch(Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/v1/vehicle/{id}/data")
    public ResponseEntity getData(@PathVariable int id) {
        try {
            List<VehicleData> dataList =  vehicleService.getVehicleDataById(id);
            return new ResponseEntity(dataList, HttpStatus.OK);
        } catch (VehicleNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch(Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/v1/vehicle/report")
    public void reportFleet(@RequestBody VehicleRegisterRequest vehicleRegistrationRequest) {
        log.info("Received new vehicle report request");
        reportService.generateReport();
    }
}
