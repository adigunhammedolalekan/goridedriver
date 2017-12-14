package goride.com.goridedriver.entities;

/**
 * Created by root on 11/15/17.
 */

public class Driver {

    private String email = "";
    private String firstName = "";
    private String lastName = "";
    private String carModel = "";
    private String licensePlateNumber = "";
    private String password = "";
    private String phoneNumber = "";
    private String driverState = "";
    private String aboutGoRide = "";

    public Driver(String email, String firstName,
                  String lastName, String carModel,
                  String licensePlateNumber, String password, String phoneNumber, String state, String about) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.carModel = carModel;
        this.licensePlateNumber = licensePlateNumber;
        this.password = password;
        this.driverState = state;
        this.aboutGoRide = about;
        this.phoneNumber = phoneNumber;
    }

    public Driver() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAboutGoRide(String aboutGoRide) {
        this.aboutGoRide = aboutGoRide;
    }

    public void setDriverState(String driverState) {
        this.driverState = driverState;
    }

    public String getAboutGoRide() {
        return aboutGoRide;
    }

    public String getDriverState() {
        return driverState;
    }
}
