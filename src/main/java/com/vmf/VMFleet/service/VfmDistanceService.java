package com.vmf.VMFleet.service;

import com.vmf.VMFleet.api.model.VehicleData;
import com.vmf.VMFleet.dao.VehicleMetric;
import com.vmf.VMFleet.dao.VfmMetrics;
import com.vmf.VMFleet.dao.VfmMetricsRepo;
import com.vmf.VMFleet.kafka.KConstants;
import com.vmf.VMFleet.kafka.serdes.vehiclemetrics.VehicleDataDes;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

import static com.vmf.VMFleet.utils.DistanceCalculator.calculateDistanceInKilometer;
import static com.vmf.VMFleet.utils.VfmMetricUtil.AggregateByMetric;

@Slf4j
@Service
public class VfmDistanceService {

    @Autowired
    VehicleDataDes vehicleDataDes;

    @Autowired
    VehicleService vehicleService;

    @Autowired
    VfmMetricsRepo metricsRepo;

    @KafkaListener(topics = KConstants.DISTANCE_TOPIC, groupId = "DistanceParser")
    public void consume(ConsumerRecord<String, String> record)
    {
        log.info(String.format("%s record: %s", this.getClass().getSimpleName(), record.value()));

        VehicleData vehicleData =
                vehicleDataDes.deserialize(record.value().getBytes(StandardCharsets.UTF_8));

        process(vehicleData);
    }

    private void process(VehicleData vehicleData) {
        VehicleMetric vehicleMetric = vehicleService.getLastPos(vehicleData.getId());
        if (vehicleMetric.getLastUpdated() != 0) {
            int distanceKm = calculateDistanceInKilometer(vehicleMetric.getLatitude(),
                                vehicleMetric.getLongitude(),
                                vehicleData.getLatitude(),
                                vehicleData.getLongitude());
            AggregateByMetric(distanceKm, VfmMetrics.MetricType.DISTANCE, vehicleData, metricsRepo);
        }
        vehicleMetric.setLatitude(vehicleData.getLatitude());
        vehicleMetric.setLongitude(vehicleData.getLongitude());
        vehicleMetric.setLastUpdated(System.currentTimeMillis());
        vehicleService.updateVehiclePos(vehicleMetric);
    }
}
