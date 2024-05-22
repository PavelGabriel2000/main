package com.pavel.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {
	
	@Autowired
	private CurrencyExchangeProxy proxy;
	
	@GetMapping("/currency-conv/from/{from}/to/{to}/quantity/{qty}")
	public CurrencyConversion calculateCurrencyConversion(
				@PathVariable String from,
				@PathVariable String to,
				@PathVariable BigDecimal qty
			) {
		
		HashMap<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		
		ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity(
				"http://localhost:8000/currency-exchange/from/{from}/to/{to}",
				CurrencyConversion.class,
				uriVariables
				);
		
		CurrencyConversion currencyConversion = responseEntity.getBody();
		
		return new CurrencyConversion(
				currencyConversion.getId(),
				from, to, qty,
				currencyConversion.getConversionMultiple(),
				qty.multiply(currencyConversion.getConversionMultiple()),
				currencyConversion.getEnvironment() + " " + "Rest Template" 
				);
				
		
	}
	
	@GetMapping("/currency-conv/feign/from/{from}/to/{to}/quantity/{qty}")
	public CurrencyConversion calculateCurrencyConversionProxy(
				@PathVariable String from,
				@PathVariable String to,
				@PathVariable BigDecimal qty
			) {
		
		CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);
		
		return new CurrencyConversion(
				currencyConversion.getId(),
				from,to,qty,
				currencyConversion.getConversionMultiple(),
				qty.multiply(currencyConversion.getConversionMultiple()),
				currencyConversion.getEnvironment() + " feign"
				);
		
	}

}
