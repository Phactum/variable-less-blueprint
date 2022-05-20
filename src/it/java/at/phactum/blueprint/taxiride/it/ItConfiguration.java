package at.phactum.blueprint.taxiride.it;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import at.phactum.blueprint.taxiride.TaxiRide;
import at.phactum.blueprint.taxiride.service.DriverService;

@Configuration
@EnableAutoConfiguration
@EnableJpaAuditing
@ComponentScan(basePackageClasses = { TaxiRide.class })
@EntityScan(basePackageClasses = { TaxiRide.class })
@EnableJpaRepositories(basePackageClasses = { TaxiRide.class })
public class ItConfiguration {

    @Lazy
    @Autowired
    private TaxiRide taxiRide;

    public static class MockDriverService implements DriverService {

        private static final LinkedBlockingQueue<String> EVENTS = new LinkedBlockingQueue<>();
        
        private final TaxiRide taxiRide;

        public MockDriverService(
                TaxiRide taxiRide) {
            
            this.taxiRide = taxiRide;
            
        }
        
        @Override
        public Collection<String> determineNearbyDrivers(
                final String pickupLocation,
                final OffsetDateTime pickupTime,
                final String targetLocation) {

            return List.of("Martin", "Stephan");

        }

        @Override
        public void requestRide(
                final String driver,
                final String rideId) {

            EVENTS.add(rideId);

        }

        public void waitForAllDriversRequestedForRides() throws Exception {

            // expect two events
            EVENTS.poll(10, TimeUnit.SECONDS);
            EVENTS.poll(10, TimeUnit.SECONDS);

            // wait for any transaction to be completed
            Thread.sleep(200);

        }

        @Override
        public void payDriver(
                final String driver,
                final float amount) {

            EVENTS.add(driver);

        }

        public void waitForDriverBeingPayed() throws Exception {

            // expect one event
            EVENTS.poll(10, TimeUnit.SECONDS);

            // wait for any transaction to be completed
            Thread.sleep(200);

        }
        
        public void confirmRide(
                final String rideId,
                final String driverId) {
            
            taxiRide.confirmRide(rideId, rideId);
            
        }

        public void finishRide(
                final String rideId) {
            
            taxiRide.finishRide(rideId);
            
        }

    }

    @Bean
    public MockDriverService mockDriverService() {

        return new MockDriverService(taxiRide);

    }

}
