package com.vmf.VMFleet.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleDataRepo extends CrudRepository<VehicleData, Integer> {
    List<VehicleData> getDataByVehicleId(int vehicleId);
}
