package com.vmf.VMFleet.kafka.serdes.vehiclemetrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmf.VMFleet.dao.VehicleData;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;

@Component
public class VehicleDataDes implements Deserializer {

	 @Override 
	 public void close() { }
	 
	@Override
	public VehicleData deserialize(String topic, byte[] data) {
		return deserialize(data);
	}

	public VehicleData deserialize(byte[] data) {
		ObjectMapper mapper = new ObjectMapper();
		VehicleData vm = null;
		try {
			vm = mapper.readValue(data, VehicleData.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vm;
	}
}
