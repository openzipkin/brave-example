package com.github.kristofa.brave.resteasyexample;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.ServiceNameProvider;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.github.kristofa.brave.http.StringServiceNameProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BraveConfig {

    @Bean
    @Scope(value = "singleton")
    public Brave brave() {
       Brave.Builder builder = new Brave.Builder();
       //builder.spanCollector(KafkaSpanCollector.create("192.168.99.100:9092", new EmptySpanCollectorMetricsHandler()));
       //builder.spanCollector(HttpSpanCollector.create("http://192.168.99.100:9411/", new EmptySpanCollectorMetricsHandler()));
       return builder.build();
    }

    @Bean
    @Scope(value = "singleton")
    public ServiceNameProvider serviceNameProvider() {
        return new StringServiceNameProvider("test_service");
    }

    @Bean
    @Scope(value = "singleton")
    public SpanNameProvider spanNameProvider() {
        return new DefaultSpanNameProvider();
    }
}
