package com.vmf.VMFleet.exceptions;

public class VehicleInActiveException extends Exception {
    public VehicleInActiveException() {
        super("Vehicle is inactivated");
    }
}
