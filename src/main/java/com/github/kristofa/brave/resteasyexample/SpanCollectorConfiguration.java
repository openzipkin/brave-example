package com.github.kristofa.brave.resteasyexample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.SpanCollector;

/**
 * {@link SpanCollector} spring dependency injection configuration.
 * 
 * @author kristof
 */
@Configuration
public class SpanCollectorConfiguration {

    @Bean
    @Scope(value = "singleton")
    public SpanCollector spanCollector() {

        // For development purposes we use the logging span collector.
        return Brave.getLoggingSpanCollector();
    }
}
