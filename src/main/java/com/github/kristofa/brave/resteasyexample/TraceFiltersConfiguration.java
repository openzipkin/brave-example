package com.github.kristofa.brave.resteasyexample;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.TraceFilters;

@Configuration
public class TraceFiltersConfiguration {

    @Bean
    @Scope(value = "singleton")
    public TraceFilters traceFilters() {
        return new TraceFilters(Arrays.asList(Brave.getTraceAllTraceFilter()));
    }

}
