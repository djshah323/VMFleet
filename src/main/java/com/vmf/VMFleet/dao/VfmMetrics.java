package com.vmf.VMFleet.dao;

import lombok.Data;

import javax.persistence.*;

@Table
@Entity
@Data
public class VfmMetrics {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column
    private int vehicleId;

    @Column
    @Enumerated(EnumType.STRING)
    private MetricType metricName;

    @Column
    @Enumerated(EnumType.STRING)
    private AggType aggType;

    @Column
    private String metricValue;

    @Column
    private int dateTimeNumber;

    public enum MetricType {
        DISTANCE,
        SPEED,
        FUEL
    }

    public enum AggType {
        DAILY,
        MONTHLY
    }
}
