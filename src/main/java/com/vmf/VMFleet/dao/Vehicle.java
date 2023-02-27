package com.vmf.VMFleet.dao;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"registrationId"}))
@Data
public class Vehicle {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column
    private String registrationId;

    @Column
    private String createdDate;

    @Column
    @Enumerated(EnumType.STRING)
    private CollectionState state;

    @Column
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Column
    private String vehicleMake;

    public enum CollectionState {
        ACTIVE,
        INACTIVE
    }

    public enum VehicleType {
        TWO,
        FOUR
    }
}
