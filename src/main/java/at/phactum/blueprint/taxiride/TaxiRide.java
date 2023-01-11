package at.phactum.blueprint.taxiride;

import at.phactum.blueprint.taxiride.domain.Ride;
import at.phactum.blueprint.taxiride.domain.RideRepository;
import at.phactum.blueprint.taxiride.events.RideBooked;
import at.phactum.blueprint.taxiride.service.DriverService;
import io.vanillabp.spi.process.ProcessService;
import io.vanillabp.spi.service.MultiInstanceIndex;
import io.vanillabp.spi.service.WorkflowService;
import io.vanillabp.spi.service.WorkflowTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;

@Service
@WorkflowService(workflowAggregateClass = Ride.class)
@Transactional
public class TaxiRide {

    @Autowired
    private ProcessService<Ride> processService;

    @Autowired
    private RideRepository rides;
    
    @Autowired
    private DriverService drivers;

    public String rideBooked(
            final RideBooked event) {
        
        final var ride = new Ride();
        ride.setPickupTime(event.getPickupTime());
        ride.setPickupLocation(event.getPickupLocation());
        ride.setTargetLocation(event.getTargetLocation());

        final var result = processService.correlateMessage(ride, event);

        return result.getId();
        
    }
    
    @WorkflowTask
    public void determineNearbyDrivers(
            final Ride ride) {
        
        final var nearbyDrivers = drivers.determineNearbyDrivers(
                ride.getPickupLocation(),
                ride.getPickupTime(),
                ride.getTargetLocation());
        
        ride.setNearbyDrivers(
                new LinkedList<>(nearbyDrivers));
        
    }
    
    @WorkflowTask
    public void requestRide(
            final Ride ride,
            final @MultiInstanceIndex("Activity_RequestRide") int currentDriver) {
        
        var driver = ride.getNearbyDrivers().get(currentDriver);
        
        drivers.requestRide(driver, ride.getId());
        
    }

    public void confirmRide(
            final String rideId,
            final String driverId) {
        
        final var ride = rides.findById(rideId).get();
        ride.setDriver(driverId);
        
        processService.correlateMessage(ride, "ConfirmRide");
        
    }
    
    public void finishRide(
            final String rideId) {
        
        final var ride = rides.findById(rideId).get();
        processService.correlateMessage(ride, "FinishRide");
        
    }
    
    @WorkflowTask
    public void payDriver(
            final Ride ride) {
        
        final var amount = 47.11f; // in this test-case a constant value

        drivers.payDriver(ride.getDriver(), amount);
        
    }

    @WorkflowTask
    public void chargeCustomer(
            final Ride ride) {

        // use payment-service-provider to charge customer

    }
    
    @WorkflowTask
    public void rideAvailable(
            final Ride ride) {
        
        // send confirmation email to passenger
        
    }            
    
    @WorkflowTask
    public void noRideAvailable(
            final Ride ride) {
        
        // send apology email to passenger
        
    }            

}
