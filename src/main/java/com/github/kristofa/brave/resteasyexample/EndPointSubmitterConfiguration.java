package com.github.kristofa.brave.resteasyexample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.EndPointSubmitter;

/**
 * Spring dependency injection configuration for {@link EndPointSubmitter}.
 * 
 * @author kristof
 */
@Configuration
public class EndPointSubmitterConfiguration {

    @Bean
    @Scope(value = "singleton")
    public EndPointSubmitter endPointSubmitter() {
        return Brave.getEndPointSubmitter();
    }
}
