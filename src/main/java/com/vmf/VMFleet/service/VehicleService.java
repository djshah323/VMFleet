package com.vmf.VMFleet.service;

import com.vmf.VMFleet.api.model.VehicleData;
import com.vmf.VMFleet.api.model.VehicleRegisterRequest;
import com.vmf.VMFleet.dao.Vehicle;
import com.vmf.VMFleet.dao.VehiclePos;
import com.vmf.VMFleet.dao.VehiclePosRepo;
import com.vmf.VMFleet.dao.VehicleRepo;
import com.vmf.VMFleet.exceptions.VehicleAlreadyExistsException;
import com.vmf.VMFleet.exceptions.VehicleInActiveException;
import com.vmf.VMFleet.exceptions.VehicleNotFoundException;
import com.vmf.VMFleet.kafka.KFleetPublisher;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepo vehicleRepo;

    @Autowired
    private VehiclePosRepo vehiclePosRepo;

    @Autowired
    private KFleetPublisher metricsPublisher;

    public Vehicle addVehicle(VehicleRegisterRequest request) throws VehicleAlreadyExistsException {
        try {
            Vehicle newVehicle = new Vehicle();
            newVehicle.setRegistrationId(request.getRegistrationId());
            newVehicle.setVehicleMake(request.getMakeType());
            newVehicle.setVehicleType(Vehicle.VehicleType.valueOf(request.getVehicleType()));
            newVehicle.setCreatedDate(new Date().toString());
            newVehicle.setState(Vehicle.CollectionState.ACTIVE);
            return vehicleRepo.save(newVehicle);
        } catch(org.springframework.dao.DataIntegrityViolationException ex) {
            throw new VehicleAlreadyExistsException();
        }
    }

    public ArrayList<Vehicle> getAllVehicle() {
        ArrayList<Vehicle> vehicleList = new ArrayList<>();
        vehicleRepo.findAll().forEach(vehicleList::add);
        return vehicleList;
    }

    public void pushNewMetric(VehicleData metrics)
            throws VehicleNotFoundException, KafkaException, VehicleInActiveException {
        Vehicle vehicle = getVehicle(metrics.getId());
        if (vehicle.getState() == Vehicle.CollectionState.INACTIVE) {
            throw new VehicleInActiveException();
        }
        if (metrics.getTimeStamp() == 0) {
            metrics.setTimeStamp(System.currentTimeMillis());
        }
        metricsPublisher.sendMetrics(metrics);
    }

    public void updateVehiclePos(VehiclePos vehiclePos) {
        vehiclePosRepo.save(vehiclePos);
    }

    public Vehicle getVehicle(int id) throws VehicleNotFoundException {
        return vehicleRepo.findById(id)
                .orElseThrow(VehicleNotFoundException::new);
    }

    public VehiclePos getLastPos(int id) {
       return vehiclePosRepo.findById(id)
               .orElse(new VehiclePos(id));
    }
}
