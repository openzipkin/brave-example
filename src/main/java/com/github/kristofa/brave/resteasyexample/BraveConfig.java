package com.github.kristofa.brave.resteasyexample;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.EmptySpanCollectorMetricsHandler;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.HttpSpanCollector;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.github.kristofa.brave.kafka.KafkaSpanCollector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BraveConfig {

    @Bean
    @Scope(value = "singleton")
    public Brave brave() {
       Brave.Builder builder = new Brave.Builder("brave-resteasy-example");
       //builder.spanCollector(KafkaSpanCollector.create("127.0.0.1:9092", new EmptySpanCollectorMetricsHandler()));
       builder.spanCollector(HttpSpanCollector.create("http://127.0.0.1:9411/", new EmptySpanCollectorMetricsHandler()));
       return builder.build();
    }

    @Bean
    @Scope(value = "singleton")
    public SpanNameProvider spanNameProvider() {
        return new DefaultSpanNameProvider();
    }
}

