package com.vmf.VMFleet.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmf.VMFleet.dao.VehicleData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.HttpServerErrorException;

@Service
@Slf4j
public class KFleetPublisher {
	
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private ObjectMapper mapper;

  public void sendMetrics(VehicleData vehicleData) throws KafkaException {
  	try {
		ListenableFuture<SendResult<String, String>> future =
				this.kafkaTemplate.send(KConstants.RAW_EVENTS_TOPIC,
						Integer.toString(vehicleData.getVehicleId()),
						mapper.writeValueAsString(vehicleData));
		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
			@Override
			public void onSuccess(SendResult<String, String> result) {
				log.info("Raw metric delivered with offset {}",
						result.getRecordMetadata().offset());
			}
			@Override
			public void onFailure(Throwable ex) {
				log.warn("Unable to deliver raw metric [{}]. {}",
						vehicleData,
						ex.getMessage());
			}
		});
	} catch (Exception ex) {
  		throw new KafkaException("Error in publish!");
	}
  }

  public void publishDistance(VehicleData vehicleData) {
	try {
		ListenableFuture<SendResult<String, String>> future =
				this.kafkaTemplate.send(KConstants.DISTANCE_TOPIC,
						Integer.toString(vehicleData.getVehicleId()),
						mapper.writeValueAsString(vehicleData));
		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
			@Override
			public void onSuccess(SendResult<String, String> result) {
				log.info("Raw metric delivered with offset {}",
						result.getRecordMetadata().offset());
			}
			@Override
			public void onFailure(Throwable ex) {
				log.warn("Unable to deliver raw metric [{}]. {}",
						vehicleData,
						ex.getMessage());
			}
		});
	} catch (Exception ex) {
		throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
	}
  }

 public void publishSpeed(VehicleData vehicleData) {
	try {
		ListenableFuture<SendResult<String, String>> future =
				this.kafkaTemplate.send(KConstants.SPEED_TOPIC,
						Integer.toString(vehicleData.getVehicleId()),
						mapper.writeValueAsString(vehicleData));
		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
			@Override
			public void onSuccess(SendResult<String, String> result) {
				log.info("Raw metric delivered with offset {}",
						result.getRecordMetadata().offset());
			}
			@Override
			public void onFailure(Throwable ex) {
				log.warn("Unable to deliver raw metric [{}]. {}",
						vehicleData,
						ex.getMessage());
			}
		});
	} catch (Exception ex) {
		throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
	}
 }

 public void publishFuel(VehicleData vehicleData) {
	try {
		ListenableFuture<SendResult<String, String>> future =
				this.kafkaTemplate.send(KConstants.FUEL_TOPIC,
						Integer.toString(vehicleData.getVehicleId()),
						mapper.writeValueAsString(vehicleData));
		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
			@Override
			public void onSuccess(SendResult<String, String> result) {
				log.info("Raw metric delivered with offset {}",
						result.getRecordMetadata().offset());
			}
			@Override
			public void onFailure(Throwable ex) {
				log.warn("Unable to deliver raw metric [{}]. {}",
						vehicleData,
						ex.getMessage());
			}
		});
	} catch (Exception ex) {
		throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
	}
 }
}
