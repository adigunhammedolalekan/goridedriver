package goride.com.goridedriver.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 11/19/17.
 */

public class Ride {

    private String ID = "";
    private String driverID = "";
    private String customerID = "";
    private double pickupLat = 0.0;
    private double pickupLon = 0.0;
    private double destinationLat = 0.0;
    private double destinationLon = 0.0;
    private String destinationAddress = "";
    private String pickupAddress = "";
    private String status = "";

    public Ride(String driverID, String customerID, double pickupLat, double pickupLon,
                double destinationLat, double destinationLon,
                String destinationAddress, String pickupAddress, String status) {
        this.driverID = driverID;
        this.customerID = customerID;
        this.pickupLat = pickupLat;
        this.pickupLon = pickupLon;
        this.destinationLat = destinationLat;
        this.destinationLon = destinationLon;
        this.destinationAddress = destinationAddress;
        this.pickupAddress = pickupAddress;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public double getDestinationLon() {
        return destinationLon;
    }

    public double getPickupLat() {
        return pickupLat;
    }

    public double getPickupLon() {
        return pickupLon;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public String getDriverID() {
        return driverID;
    }

    public String getID() {
        return ID;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public String  toJson() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", ID);
        jsonObject.put("destination_latitude", getDestinationLat());
        jsonObject.put("destination_longitude", getDestinationLon());
        jsonObject.put("pickup_latitude", getPickupLat());
        jsonObject.put("pickup_longitude", getPickupLon());
        jsonObject.put("customer_id", getCustomerID());
        jsonObject.put("destination_address", getDestinationAddress());
        jsonObject.put("pickup_address", getPickupAddress());
        jsonObject.put("driver_id", getDriverID());
        jsonObject.put("status", getStatus());
        return jsonObject.toString();
    }
    public static Ride from(String s) throws JSONException {

        JSONObject jsonObject = new JSONObject(s);
        return new Ride(jsonObject.getString("driver_id"), jsonObject.getString("customer_id"),
                jsonObject.getDouble("pickup_latitude"), jsonObject.getDouble("pickup_longitude"),
                jsonObject.getDouble("destination_latitude"), jsonObject.getDouble("destination_longitude"),
                jsonObject.getString("destination_address"), jsonObject.getString("pickup_address"), jsonObject.getString("status"));
    }
}
