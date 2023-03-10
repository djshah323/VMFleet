package com.vmf.VMFleet.service;

import com.vmf.VMFleet.dao.VehicleData;
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
public class VfmFuelService {
    @Autowired
    VehicleService vehicleService;

    @Autowired
    VehicleDataDes vehicleDataDes;

    @Autowired
    VfmMetricsRepo metricsRepo;

    public final int LOW_FUEL = 10;

    @KafkaListener(topics = KConstants.FUEL_TOPIC, groupId = "FuelParser")
    public void consume(ConsumerRecord<String, String> record)
    {
        log.info(String.format("%s record: %s", this.getClass().getSimpleName(), record.value()));

        VehicleData vehicleData =
                vehicleDataDes.deserialize(record.value().getBytes(StandardCharsets.UTF_8));

        process(vehicleData);
    }

    private void process(VehicleData vehicleData) {
        int fuel = vehicleData.getFuel();
        if (fuel < LOW_FUEL) {
            AggregateByMetric(1, VfmMetrics.MetricType.FUEL_LOW, vehicleData, metricsRepo);
        }
    }
}
