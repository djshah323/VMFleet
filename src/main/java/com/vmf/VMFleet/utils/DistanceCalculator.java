package com.vmf.VMFleet.utils;

public class DistanceCalculator {
    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

    /**
     * Calculates distance between two points on earth.
     * @param srcLat source latitude
     * @param srcLng source longitude
     * @param dstLat destination latitude
     * @param dstLng destination longitude
     * @return distance
     */
    public static int calculateDistanceInKilometer(double srcLat, double srcLng,
                                            double dstLat, double dstLng) {
        double latDistance = Math.toRadians(srcLat - dstLat);
        double lngDistance = Math.toRadians(srcLng - dstLng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(srcLat)) * Math.cos(Math.toRadians(srcLng))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }
}
