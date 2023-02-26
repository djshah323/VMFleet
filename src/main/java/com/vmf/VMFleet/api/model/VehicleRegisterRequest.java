package com.vmf.VMFleet.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class VehicleRegisterRequest {
    private String registrationId;
    private String vehicleType;
    private String makeType;
}
