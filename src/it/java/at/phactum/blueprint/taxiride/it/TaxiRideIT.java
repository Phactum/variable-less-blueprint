package at.phactum.blueprint.taxiride.it;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionTemplate;

import at.phactum.blueprint.taxiride.TaxiRide;
import at.phactum.blueprint.taxiride.events.RideBooked;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ItConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port=8080",
                "spring.application.name=IT",
                "workerId=IT",
                "spring.main.allow-bean-definition-overriding=true"
            }
    )
public class TaxiRideIT {
    
    @Autowired
    private TransactionTemplate transaction;
    
    @Autowired
    private TaxiRide taxiRide;
    
    @Test
    public void testRide() {
        
        transaction.executeWithoutResult(status -> {
            
            final var event = new RideBooked()
                    .pickupTime(OffsetDateTime.now())
                    .pickupLocation("start-road 47")
                    .targetLocation("end-road 11");
            
            taxiRide.rideBooked(event);
            
        });
        
    }
    
}
