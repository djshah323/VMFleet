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

import static com.vmf.VMFleet.utils.VfmMetricUtil.AggregateByMetric;

@Slf4j
@Service
public class VfmSpeedService {
    @Autowired
    VehicleService vehicleService;

    @Autowired
    VehicleDataDes vehicleDataDes;

    @Autowired
    VfmMetricsRepo metricsRepo;

    public final int SPEED_LIMIT = 100;

    @KafkaListener(topics = KConstants.SPEED_TOPIC, groupId = "SpeedParser")
    public void consume(ConsumerRecord<String, String> record)
    {
        log.info(String.format("record: %s", record.value()));
        VehicleData vehicleData =
                vehicleDataDes.deserialize(record.value().getBytes(StandardCharsets.UTF_8));
        process(vehicleData);
    }

    private void process(VehicleData vehicleData) {
        int speed = vehicleData.getSpeed();
        VehicleMetric vehicleMetric = vehicleService.getLastPos(vehicleData.getId());
        vehicleMetric.setSpeed(speed);
        vehicleMetric.setLastUpdated(System.currentTimeMillis());
        vehicleService.updateVehiclePos(vehicleMetric);
        if (speed > SPEED_LIMIT) {
            AggregateByMetric(1, VfmMetrics.MetricType.SPEED, vehicleData, metricsRepo);
        }
    }
}
