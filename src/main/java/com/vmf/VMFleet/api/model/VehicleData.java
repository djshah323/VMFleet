package com.vmf.VMFleet.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class VehicleData {
    private int id;
    private double latitude;
    private double longitude;
    private int fuel;
    private int speed;
    private long timeStamp;

    public VehicleData() { }

    public VehicleData distanceMetrics() {
      return new VehicleDataBuilder()
                .id(this.id).latitude(this.latitude)
                .longitude(this.longitude)
                .timeStamp(this.timeStamp)
                .build();
    }

    public VehicleData speedMetrics() {
        return new VehicleDataBuilder()
                .id(this.id).speed(this.speed)
                .timeStamp(this.timeStamp)
                .build();
    }

    public VehicleData fuelMetrics() {
        return new VehicleDataBuilder()
                .id(this.id).fuel(this.fuel)
                .timeStamp(this.timeStamp)
                .build();
    }
}
