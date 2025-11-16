package config;

import adapter.GenericJpaCrudRepositoryAdapter;
import domain.model.*;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import port.outbound.CRUDRepositoryPort;

@Configuration
public class RepositoryConfig {
    private final EntityManager em;

    public RepositoryConfig(EntityManager em) {
        this.em = em;
    }

    @Bean
    public CRUDRepositoryPort<Route> routeRepository() {
        return new GenericJpaCrudRepositoryAdapter<>(em, Route.class);
    }

    @Bean
    public CRUDRepositoryPort<Stops> stopRepository() {
        return new GenericJpaCrudRepositoryAdapter<>(em, Stops.class);
    }

    @Bean
    public CRUDRepositoryPort<RouteStops> routeStopRepository() {
        return new GenericJpaCrudRepositoryAdapter<>(em, RouteStops.class);
    }

    @Bean
    public CRUDRepositoryPort<Frequency> frequencyRepository() {
        return new GenericJpaCrudRepositoryAdapter<>(em, Frequency.class);
    }

    @Bean
    public CRUDRepositoryPort<ExceptionEntry> exceptionEntryRepository() {
        return new GenericJpaCrudRepositoryAdapter<>(em, ExceptionEntry.class);
    }

    @Bean
    public CRUDRepositoryPort<Season> seasonRepository() {
        return new GenericJpaCrudRepositoryAdapter<>(em, Season.class);
    }

    @Bean
    public CRUDRepositoryPort<OperationMessage> operationMessageRepository() {
        return new GenericJpaCrudRepositoryAdapter<>(em, OperationMessage.class);
    }

}
