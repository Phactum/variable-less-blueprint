package at.phactum.blueprint.taxiride.config;

import io.vanillabp.springboot.adapter.BpDeploymentConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "taxiride")
public class ApplicationProperties implements BpDeploymentConfiguration {

    private String processesLocation;

    @Override
    public String getProcessesLocation() {
        return processesLocation;
    }

    public void setProcessesLocation(String processesLocation) {
        this.processesLocation = processesLocation;
    }

}
