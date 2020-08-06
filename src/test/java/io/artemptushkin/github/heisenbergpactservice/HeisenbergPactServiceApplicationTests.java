package io.artemptushkin.github.heisenbergpactservice;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Provider("heisenberg")
@PactBroker(
		host = "hello.pact.dius.com.au", scheme = "https",
		authentication = @PactBrokerAuth(token = "GJADqmiVcrtQu5rjyxpjIQ"),
		tags = {"adhoc-test"}
)
@ExtendWith(value = SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class HeisenbergPactServiceApplicationTests {

	@TestTemplate
	@ExtendWith(PactVerificationSpringProvider.class)
	void pactVerificationTestTemplate(PactVerificationContext context) {
		context.verifyInteraction();
	}

	@State("get crystals")
	public void test() { }
}
