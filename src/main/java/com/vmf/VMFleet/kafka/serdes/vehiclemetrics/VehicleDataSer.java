package com.vmf.VMFleet.kafka.serdes.vehiclemetrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmf.VMFleet.api.model.VehicleData;
import org.apache.kafka.common.serialization.Serializer;

public class VehicleDataSer implements Serializer {
	
	 @Override public void close() {
		 
	 }
	 
	@Override
	public byte[] serialize(String topic, Object data) {
		 byte[] retVal = null;
		 ObjectMapper objectMapper = new ObjectMapper();
		 try {
		     retVal = objectMapper
		    		 .writeValueAsString((VehicleData)data)
		    		 .getBytes();
		 } catch (Exception e) {
		     e.printStackTrace();
		 }
		 return retVal;
	}
}
