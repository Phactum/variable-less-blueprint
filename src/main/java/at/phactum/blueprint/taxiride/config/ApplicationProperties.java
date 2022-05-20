package at.phactum.blueprint.taxiride.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import at.phactum.bp.blueprint.bpm.deployment.BpDeploymentConfiguration;
import at.phactum.bp.blueprint.modules.ModuleSpecificProperties;
import at.phactum.bp.blueprint.modules.WorkflowModuleIdAwareProperties;

@Configuration
@ConfigurationProperties(prefix = ApplicationProperties.WORKFLOW_MODULE_ID)
public class ApplicationProperties implements WorkflowModuleIdAwareProperties, BpDeploymentConfiguration {

    public static final String WORKFLOW_MODULE_ID = "taxiride";

    private String processesLocation;

    @Bean
    public static ModuleSpecificProperties rideModuleProperties() {

        return new ModuleSpecificProperties(ApplicationProperties.class, WORKFLOW_MODULE_ID);

    }

    @Override
    public String getWorkflowModuleId() {

        return WORKFLOW_MODULE_ID;

    }

    @Override
    public String getProcessesLocation() {
        return processesLocation;
    }

    public void setProcessesLocation(String processesLocation) {
        this.processesLocation = processesLocation;
    }

}
