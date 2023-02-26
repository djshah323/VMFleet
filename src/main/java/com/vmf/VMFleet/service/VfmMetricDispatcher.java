package com.vmf.VMFleet.service;

import com.vmf.VMFleet.api.model.VehicleData;
import com.vmf.VMFleet.kafka.KFleetPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Dispatches the metrics based on position, speed, fuel etc.
 */
@Service
public class VfmMetricDispatcher {

    @Autowired
    KFleetPublisher kFleetPublisher;

    public void process(VehicleData metrics) {
        Optional.of(metrics).map(value-> {
                kFleetPublisher.publishDistance(value.distanceMetrics());
                return value; })
            .map(value -> {
                kFleetPublisher.publishSpeed(value.speedMetrics());
                return value;})
            .map(value -> {
                kFleetPublisher.publishFuel(value.fuelMetrics());
                return value;});
    }
}
