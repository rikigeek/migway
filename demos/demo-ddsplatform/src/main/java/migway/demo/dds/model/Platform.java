package migway.demo.dds.model;

public class Platform {

    /**
     * Maximum speed (m/s) in forward mode. 1m/s = 3.6km/h
     */
    private static final double MAX_FWD_SPEED = 30;

    /**
     * Maximum speed (m/s) in reverse mode
     */
    private static final double MAX_REV_SPEED = 2;

    // Orientation in deg
    private double pitch, roll, yaw;

    // Position. Height is in meter
    private double latitude, longitude, height;

    // Total distance in meters
    private double distanceTravelled;

    // Current speed in m/s
    private double currentSpeed;

    public String getIdentification() {
        return "Platform 0";
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getRoll() {
        return roll;
    }

    public void setRoll(double roll) {
        this.roll = roll;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(double distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public void turn(double i) {
        this.yaw = (this.yaw + i) % 360;
    }

    public void speedUp(double i) {
        // Don't prevent negative values: reverse
        this.currentSpeed += i;
        if (this.currentSpeed > MAX_FWD_SPEED)
            this.currentSpeed = MAX_FWD_SPEED;
        if (this.currentSpeed < -MAX_REV_SPEED)
            this.currentSpeed = -MAX_REV_SPEED;
    }

    public void move() {
        move(1);
    }

    public void move(double d) {
        longitude += Math.cos(Math.PI / 180 * yaw) * currentSpeed * d;
        latitude += Math.sin(Math.PI / 180 * yaw) * currentSpeed * d;
    }

    public void pitch(double d) {
        this.pitch = (this.pitch + d) % 360;
    }

    public void roll(double d) {
        this.roll = (this.roll + d) % 360;
    }

}
