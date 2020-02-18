package com.spiralforge.forxtransfer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author Sujal
 * 
 *         RestConfig
 * 
 * @description getRestTemplate() method has been created to create bean of
 *              RestTemplate
 *
 */

@Configuration
public class RestConfig {

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

}
