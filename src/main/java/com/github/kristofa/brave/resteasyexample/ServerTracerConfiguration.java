package com.github.kristofa.brave.resteasyexample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.ServerTracer;

/**
 * {@link ServerTracer} spring dependency injection configuration.
 * 
 * @author kristof
 */
@Configuration
@Import({SpanCollectorConfiguration.class})
public class ServerTracerConfiguration {

    @Autowired
    private SpanCollectorConfiguration spanCollectorConfig;

    @Bean
    @Scope(value = "singleton")
    public ServerTracer serverTracer() {
        return Brave.getServerTracer(spanCollectorConfig.spanCollector());
    }
}
