package com.github.kristofa.brave.resteasyexample;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Sets up resteasy spring integration.
 * 
 * @author kristof
 */
@Configuration
@ImportResource({"classpath:springmvc-resteasy.xml"})
public class ResteasySpringConfig {

}
