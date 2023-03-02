package com.vmf.VMFleet.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Data
@AllArgsConstructor
@Builder
@Entity
@Table
public class VehicleData {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column
    private int vehicleId;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private int fuel;

    @Column
    private int speed;

    @Column
    private long timeStamp;

    public VehicleData() { }

    public VehicleData distanceMetrics() {
      return new VehicleData.VehicleDataBuilder()
                .vehicleId(this.vehicleId).latitude(this.latitude)
                .longitude(this.longitude)
                .timeStamp(this.timeStamp)
                .build();
    }

    public VehicleData speedMetrics() {
        return new VehicleData.VehicleDataBuilder()
                .vehicleId(this.vehicleId).speed(this.speed)
                .timeStamp(this.timeStamp)
                .build();
    }

    public VehicleData fuelMetrics() {
        return new VehicleData.VehicleDataBuilder()
                .vehicleId(this.vehicleId).fuel(this.fuel)
                .timeStamp(this.timeStamp)
                .build();
    }
}
