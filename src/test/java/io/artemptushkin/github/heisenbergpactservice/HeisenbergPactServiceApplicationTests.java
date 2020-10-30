package io.artemptushkin.github.heisenbergpactservice;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Provider("heisenberg")
@PactBroker(
		host = "hello.pact.dius.com.au", scheme = "https",
		authentication = @PactBrokerAuth(token = "GJADqmiVcrtQu5rjyxpjIQ")
)
@ExtendWith(value = {SpringExtension.class, PactConsumerTestExt.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class HeisenbergPactServiceApplicationTests {

	@TestTemplate
	@PactTestFor(port = "8092", providerName = "gustavo-fring")
	@ExtendWith(PactVerificationSpringProvider.class)
	void pactVerificationTestTemplate(PactVerificationContext context) {
		context.verifyInteraction();
	}

	@Pact(provider = "gustavo-fring", consumer = "heisenberg")
	public RequestResponsePact createPact(PactDslWithProvider pactDsl) {
		return pactDsl
				.given("say-my-name")
				.uponReceiving("GET REQUEST")
				.path("/fring/say-my-name")
				.willRespondWith()
				.status(200)
				.body(new PactDslJsonBody()
						.stringValue("name", "heisenberg")
				)
				.toPact();
	}

	@State("get crystals")
	public void test() { }
}
