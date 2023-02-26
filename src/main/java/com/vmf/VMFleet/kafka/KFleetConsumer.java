package com.vmf.VMFleet.kafka;

import com.vmf.VMFleet.api.model.VehicleData;
import com.vmf.VMFleet.kafka.serdes.vehiclemetrics.VehicleDataDes;
import com.vmf.VMFleet.service.VfmMetricDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class KFleetConsumer {

    @Autowired
    private VfmMetricDispatcher dispatcher;

    @Autowired
    VehicleDataDes vehicleDataDes;

    @KafkaListener(topics = KConstants.RAW_EVENTS_TOPIC,
            groupId = "MetricParser")
    public void consume(ConsumerRecord<String, String> record)
    {
    	log.info(String.format("record: %s", record.value()));
        VehicleData vehicleData =
                vehicleDataDes.deserialize(record.value().getBytes(StandardCharsets.UTF_8));
    	dispatcher.process(vehicleData);
    }

}
