package at.phactum.blueprint.taxiride;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.phactum.blueprint.taxiride.domain.Ride;
import at.phactum.blueprint.taxiride.events.RideBooked;
import at.phactum.blueprint.taxiride.service.DriverService;
import at.phactum.bp.blueprint.process.ProcessService;
import at.phactum.bp.blueprint.service.MultiInstanceIndex;
import at.phactum.bp.blueprint.service.WorkflowService;
import at.phactum.bp.blueprint.service.WorkflowTask;

@Service
@WorkflowService(workflowAggregateClass = Ride.class)
@Transactional
public class TaxiRide {

    @Autowired
    private ProcessService<Ride> processService;
    
    @Autowired
    private DriverService drivers;

    public void rideBooked(
            final RideBooked event) {
        
        final var ride = new Ride();
        ride.setPickupTime(event.getPickupTime());
        ride.setPickupLocation(event.getPickupLocation());
        ride.setTargetLocation(event.getTargetLocation());

        processService.correlateMessage(ride, event);
        
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
