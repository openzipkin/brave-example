package com.github.kristofa.brave.resteasyexample;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.SpanNameProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Reporter;
import zipkin.reporter.Sender;
import zipkin.reporter.okhttp3.OkHttpSender;

@Configuration
public class BraveConfig {
  /** Configuration for how to send spans to Zipkin */
  @Bean public Sender sender() {
    return OkHttpSender.create("http://127.0.0.1:9411/api/v1/spans");
    // return KafkaSender.create("127.0.0.1:9092");
  }

  /** Configuration for how to buffer spans into messages for Zipkin */
  @Bean public Reporter<Span> reporter() {
    return AsyncReporter.builder(sender()).build();
  }

  @Bean @Scope public Brave brave() {
    return new Brave.Builder("brave-resteasy-example").reporter(reporter()).build();
  }

  @Bean @Scope public SpanNameProvider spanNameProvider() {
    return new DefaultSpanNameProvider();
  }
}
