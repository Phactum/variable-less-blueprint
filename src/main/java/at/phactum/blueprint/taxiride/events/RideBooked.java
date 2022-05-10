package at.phactum.blueprint.taxiride.events;

import java.time.OffsetDateTime;

public class RideBooked {

    private String pickupLocation;

    private OffsetDateTime pickupTime;

    private String targetLocation;

    public RideBooked pickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
        return this;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public RideBooked pickupTime(OffsetDateTime pickupTime) {
        this.pickupTime = pickupTime;
        return this;
    }

    public OffsetDateTime getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(OffsetDateTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public RideBooked targetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
        return this;
    }

    public String getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
    }

}
