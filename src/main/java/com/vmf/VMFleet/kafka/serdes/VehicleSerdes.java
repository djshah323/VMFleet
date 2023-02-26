package com.vmf.VMFleet.kafka.serdes;

import com.vmf.VMFleet.dao.Vehicle;
import com.vmf.VMFleet.kafka.serdes.vehiclemetrics.VehicleDataDes;
import com.vmf.VMFleet.kafka.serdes.vehiclemetrics.VehicleDataSer;
import org.apache.kafka.common.serialization.Serdes.WrapperSerde;

public class VehicleSerdes extends WrapperSerde<Vehicle> {

	public VehicleSerdes() {
        super(new VehicleDataSer(), new VehicleDataDes());
    }
}
