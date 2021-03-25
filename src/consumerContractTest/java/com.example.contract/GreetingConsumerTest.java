package com.example.contract;


import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.example.dto.GreetingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GreetingConsumerTest {
    private static final String SERVICE_PROVIDER = "sample-provider";
    private static final String SERVICE_CONSUMER = "sample-consumer";
    private static final String PACT_BRKER = "HOST";

    public GreetingConsumerTest() throws IOException{

    }
    ObjectMapper objectMapper = new ObjectMapper();

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2(SERVICE_PROVIDER,this);



    @Pact(consumer = SERVICE_CONSUMER)
    public RequestResponsePact testGetGreetingContract(PactDslWithProvider builder) throws Exception {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return builder
                .given("testGetGreetingContract")
                .uponReceiving("Request To retrieve sample greeting")
                .path("/greeting")
                .query("name=rj")
                .method(HttpMethod.GET.name())
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .headers(headers)
                .body(objectMapper.writeValueAsString(GreetingResponse.builder().greeting("welcome rj").build()))
                .toPact();
    }

    @Test
    @PactVerification(value = SERVICE_PROVIDER, fragment = "testGetGreetingContract")
    public void getPromotionDetailsWithValidPromotionId() throws Exception {
        ResponseEntity<GreetingResponse> response = new RestTemplate()
                .getForEntity(mockProvider.getUrl() + "/greeting?name=rj", GreetingResponse.class);
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

    }

}

