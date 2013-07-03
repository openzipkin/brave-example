package com.github.kristofa.brave.resteasyexample;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.ClientTracer;

/**
 * Spring dependency injection configuration for {@link ClientTracer}.
 * 
 * @author kristof
 */
@Configuration
@Import({SpanCollectorConfiguration.class})
public class ClientTracerConfiguration {

    @Autowired
    private SpanCollectorConfiguration spanCollectorConfig;

    @Bean
    @Scope(value = "singleton")
    public ClientTracer clientTracer() {
        // For development purposes we decide to trace all requests: Brave.getTraceAllTraceFilter().
        return Brave.getClientTracer(spanCollectorConfig.spanCollector(), Arrays.asList(Brave.getTraceAllTraceFilter()));
    }
}
