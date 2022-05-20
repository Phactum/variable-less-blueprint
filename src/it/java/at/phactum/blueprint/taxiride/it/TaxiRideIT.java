package at.phactum.blueprint.taxiride.it;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionTemplate;

import at.phactum.blueprint.taxiride.TaxiRide;
import at.phactum.blueprint.taxiride.events.RideBooked;
import at.phactum.blueprint.taxiride.it.ItConfiguration.MockDriverService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ItConfiguration.class)
@ActiveProfiles("camunda7")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = { "workerId=IT" })
public class TaxiRideIT {
    
    @Autowired
    private TransactionTemplate transaction;
    
    @Autowired
    private TaxiRide taxiRide;
    
    @Autowired
    private MockDriverService drivers;

    @Test
    public void testRide() throws Exception {
        
        final var rideId = new String[1];

        transaction.executeWithoutResult(status -> {
            
            final var event = new RideBooked()
                    .pickupTime(OffsetDateTime.now())
                    .pickupLocation("start-road 47")
                    .targetLocation("end-road 11");
            
            rideId[0] = taxiRide.rideBooked(event);
            
        });
        
        drivers.waitForAllDriversRequestedForRides();

        transaction.executeWithoutResult(status -> {

            drivers.confirmRide(rideId[0], "Stephan");

        });

        // wait a second until receive task gets active
        Thread.sleep(1000);

        transaction.executeWithoutResult(status -> {

            drivers.finishRide(rideId[0]);

        });

        drivers.waitForDriverBeingPayed();

    }
    
}
