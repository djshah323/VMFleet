package com.vmf.VMFleet.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VfmMetricsRepo extends CrudRepository<VfmMetrics, UUID> {
    List<VfmMetrics> getMetricsByVehicleId(int vehicleId);
}
