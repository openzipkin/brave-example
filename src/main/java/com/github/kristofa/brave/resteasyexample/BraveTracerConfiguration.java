package com.github.kristofa.brave.resteasyexample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.BraveTracer;
import com.github.kristofa.brave.ClientTracer;
import com.github.kristofa.brave.EndPointSubmitter;
import com.github.kristofa.brave.ServerTracer;
import com.github.kristofa.brave.SpanCollector;
import com.github.kristofa.brave.TraceFilters;

@Configuration
public class BraveTracerConfiguration {
	@Autowired
	SpanCollector spanCollector;
	
	@Autowired
	TraceFilters traceFilters;
    
	@Bean
	@Scope(value = "singleton")
	public ClientTracer clientTracer()
	{
		return Brave.getClientTracer(spanCollector, traceFilters.getTraceFilters());
	}
	
	@Bean
	@Scope(value = "singleton")
	public ServerTracer serverTracer()
	{
		return Brave.getServerTracer(spanCollector, traceFilters.getTraceFilters());
	}
	
	@Bean
	public EndPointSubmitter endPointSubmitter()
	{
		return Brave.getEndPointSubmitter();
	}
	
	@Bean
	@Scope(value = "singleton")
	public BraveTracer braveTracer()
	{
		return new BraveTracer(
				clientTracer(),
				serverTracer(),
				endPointSubmitter());
	}
}
