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
import org.springframework.data.util.Pair;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static com.vmf.VMFleet.utils.VfmMetricUtil.createNew;

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
        log.info(String.format("record: %s", record.value()));
        VehicleData vehicleData =
                vehicleDataDes.deserialize(record.value().getBytes(StandardCharsets.UTF_8));
        process(vehicleData);
    }

    private void process(VehicleData vehicleData) {
        int fuel = vehicleData.getFuel();
        VehicleMetric vehicleMetric = vehicleService.getLastPos(vehicleData.getId());
        vehicleMetric.setFuel(fuel);
        vehicleMetric.setLastUpdated(System.currentTimeMillis());
        vehicleService.updateVehiclePos(vehicleMetric);
        if (fuel < LOW_FUEL) {
            final Date today = new Date(vehicleData.getTimeStamp());
            int dayIndex = today.getDate();
            int monthIndex = today.getMonth();
            Pair<VfmMetrics, VfmMetrics> pairOfVfmMetrics =
                    createNew(vehicleData.getId(), dayIndex, monthIndex, VfmMetrics.MetricType.FUEL);
            List<VfmMetrics> metricsList =
                    metricsRepo.getMetricsByVehicleId(vehicleData.getId());
            if (metricsList != null && !metricsList.isEmpty()) {
                metricsList.stream()
                        .filter(monthMetric -> (monthMetric.getAggType() == VfmMetrics.AggType.MONTHLY
                                && monthMetric.getDateTimeNumber() == monthIndex))
                        .findFirst()
                        .ifPresentOrElse(monthMetric -> {
                            pairOfVfmMetrics.getSecond().setId(monthMetric.getId());
                            pairOfVfmMetrics.getSecond().setMetricValue(Integer.toString(
                                    Integer.parseInt(monthMetric.getMetricValue()) + 1));
                            metricsList.stream()
                                    .filter(dayMetric -> (dayMetric.getAggType() == VfmMetrics.AggType.DAILY
                                            && dayMetric.getDateTimeNumber() == dayIndex))
                                    .findFirst()
                                    .ifPresentOrElse(dayMetric -> {
                                        pairOfVfmMetrics.getFirst().setId(dayMetric.getId());
                                        pairOfVfmMetrics.getFirst().setMetricValue(Integer.toString(
                                                Integer.parseInt(dayMetric.getMetricValue()) + 1));
                                    }, () -> {
                                        pairOfVfmMetrics.getFirst().setMetricValue(Integer.toString(1));
                                    });
                        }, () -> {
                            metricsList.forEach(metric-> metricsRepo.delete(metric));
                            pairOfVfmMetrics.getFirst().setMetricValue(Integer.toString(1));
                            pairOfVfmMetrics.getSecond().setMetricValue(Integer.toString(1));
                        });
            } else {
                pairOfVfmMetrics.getFirst().setMetricValue(Integer.toString(1));
                pairOfVfmMetrics.getSecond().setMetricValue(Integer.toString(1));
            }
            metricsRepo.save(pairOfVfmMetrics.getFirst());
            metricsRepo.save(pairOfVfmMetrics.getSecond());
        }
    }
}
