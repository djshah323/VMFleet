package com.vmf.VMFleet.exceptions;

public class VehicleAlreadyExistsException extends Exception {
    public VehicleAlreadyExistsException() {
        super("Vehicle already registered");
    }
}
