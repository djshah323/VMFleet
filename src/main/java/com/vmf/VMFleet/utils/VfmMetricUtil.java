package com.vmf.VMFleet.utils;

import com.vmf.VMFleet.api.model.VehicleData;
import com.vmf.VMFleet.dao.VfmMetrics;
import com.vmf.VMFleet.dao.VfmMetricsRepo;
import org.springframework.data.util.Pair;

import java.util.Date;
import java.util.List;

public class VfmMetricUtil {

    /**
     * Aggregates value in daily montly db rows for the specified metrics.
     * @param aggregatedValue value that aggregates on daily/monthly basis.
     * @param metricType Metric type - distance, speed, fuel.
     * @param vehicleData vehicle data from IOT.
     * @param metricsRepo repository handle.
     */
    public static void AggregateByMetric(int aggregatedValue, VfmMetrics.MetricType metricType,
                                         VehicleData vehicleData, VfmMetricsRepo metricsRepo) {
        final Date today = new Date(vehicleData.getTimeStamp());
        int dayIndex = today.getDate();
        int monthIndex = today.getMonth();
        Pair<VfmMetrics, VfmMetrics> pairOfVfmMetrics =
                createNew(vehicleData.getId(), dayIndex, monthIndex, metricType);
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
                                Integer.parseInt(monthMetric.getMetricValue()) + aggregatedValue));
                        metricsList.stream()
                                .filter(dayMetric -> (dayMetric.getAggType() == VfmMetrics.AggType.DAILY
                                        && dayMetric.getDateTimeNumber() == dayIndex))
                                .findFirst()
                                .ifPresentOrElse(dayMetric -> {
                                    pairOfVfmMetrics.getFirst().setId(dayMetric.getId());
                                    pairOfVfmMetrics.getFirst().setMetricValue(Integer.toString(
                                            Integer.parseInt(dayMetric.getMetricValue()) + aggregatedValue));
                                }, () -> {
                                    pairOfVfmMetrics.getFirst().setMetricValue(Integer.toString(aggregatedValue));
                                });
                    }, () -> {
                        metricsList.forEach(metric-> metricsRepo.delete(metric));
                        pairOfVfmMetrics.getFirst().setMetricValue(Integer.toString(aggregatedValue));
                        pairOfVfmMetrics.getSecond().setMetricValue(Integer.toString(aggregatedValue));
                    });
        } else {
            pairOfVfmMetrics.getFirst().setMetricValue(Integer.toString(aggregatedValue));
            pairOfVfmMetrics.getSecond().setMetricValue(Integer.toString(aggregatedValue));
        }
        metricsRepo.save(pairOfVfmMetrics.getFirst());
        metricsRepo.save(pairOfVfmMetrics.getSecond());
    }

    public static Pair<VfmMetrics, VfmMetrics> createNew(int vehicleId, int dayIndex,
                           int monthIndex, VfmMetrics.MetricType metricType) {
        VfmMetrics byDay = new VfmMetrics();
        byDay.setVehicleId(vehicleId);
        byDay.setMetricName(metricType);
        byDay.setAggType(VfmMetrics.AggType.DAILY);
        byDay.setDateTimeNumber(dayIndex);
        VfmMetrics byMonth = new VfmMetrics();
        byMonth.setMetricName(metricType);
        byMonth.setAggType(VfmMetrics.AggType.MONTHLY);
        byMonth.setVehicleId(vehicleId);
        byMonth.setDateTimeNumber(monthIndex);
        return Pair.of(byDay, byMonth);
    }
}
