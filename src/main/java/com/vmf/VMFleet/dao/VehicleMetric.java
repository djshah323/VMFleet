package com.vmf.VMFleet.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Data
@NoArgsConstructor
public class VehicleMetric {
    @Id
    @Column
    private int id;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private int speed;

    @Column
    private int fuel;

    @Column
    private long lastUpdated;

    public VehicleMetric(int id) {
        this.id = id;
    }
}
