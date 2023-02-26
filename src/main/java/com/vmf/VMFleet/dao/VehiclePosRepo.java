package com.vmf.VMFleet.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiclePosRepo extends CrudRepository<VehicleMetric, Integer> { }
