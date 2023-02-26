package com.vmf.VMFleet.api;

import com.vmf.VMFleet.api.model.VehicleData;
import com.vmf.VMFleet.api.model.VehicleRegisterRequest;
import com.vmf.VMFleet.dao.Vehicle;
import com.vmf.VMFleet.exceptions.VehicleInActiveException;
import com.vmf.VMFleet.exceptions.VehicleNotFoundException;
import com.vmf.VMFleet.service.ReportService;
import com.vmf.VMFleet.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;

@RestController
@RequestMapping("api/vmf")
@Slf4j
public class FleetController {

    @Autowired
    VehicleService vehicleService;

    @Autowired
    ReportService reportService;

    @PostMapping("/v1/vehicle")
    public Vehicle addVehicleToFleet(@RequestBody VehicleRegisterRequest vehicleRegistrationRequest) {
        log.info("Received new vehicle request");
        return vehicleService.addVehicle(vehicleRegistrationRequest);
    }

    @GetMapping("/v1/vehicle")
    public ArrayList<Vehicle> getAllVehicles() {
        log.info("Received query vehicle request");
        return vehicleService.getAllVehicle();
    }

    @PostMapping("/v1/vehicle/metric")
    public void pushMetric(@RequestBody VehicleData metrics) {
        try {
            vehicleService.pushNewMetric(metrics);
            log.info(String.format("%s metrics published", metrics.getId()));
        } catch (VehicleNotFoundException ex) {
            throw new HttpServerErrorException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (VehicleInActiveException ex) {
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (KafkaException ex) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping("/v1/vehicle/report")
    public void reportFleet(@RequestBody VehicleRegisterRequest vehicleRegistrationRequest) {
        log.info("Received new vehicle report request");
        reportService.generateReport();
    }
}
