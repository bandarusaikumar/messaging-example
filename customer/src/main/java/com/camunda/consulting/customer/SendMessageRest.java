package com.camunda.consulting.customer;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SendMessageRest implements JavaDelegate {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageRest.class);
	private static final String HOST_ORDER_FULFILLER = "localhost:8081";
	private static final String REST_ENDPOINT = "http://" + HOST_ORDER_FULFILLER + "/rest/message";

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String messageName = (String) execution.getVariable("messageName");
		String businessKey = execution.getProcessBusinessKey();

		LOGGER.info("Sending message {} with business key contraint {}", messageName, businessKey);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<RestMessageDto> entity = new HttpEntity<RestMessageDto>(//
				new RestMessageDto(messageName, businessKey), // here is the JSON body
				headers);

		ResponseEntity<Object> result = new RestTemplate().postForEntity(REST_ENDPOINT, entity, null);

		if (!result.getStatusCode().is2xxSuccessful()) {
			throw new RuntimeException(result.getStatusCode().toString() + ": " + result.getBody());
		}
	}

}