package io.artemptushkin.github.heisenbergpactservice.controller;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/crystals/v4")
@RequiredArgsConstructor
public class CrystalController {

	private final RestTemplate restTemplate;

	@GetMapping
	public CrystalsResponse get(@RequestParam Integer amount) {
		FringResponse fringResponse = restTemplate
				.getForEntity("http://localhost:8092/fring/say-my-name", FringResponse.class)
				.getBody();
		if (fringResponse != null && fringResponse.getName().equals("heisenberg")) {
			BigDecimal price = new BigDecimal("10.0");
			List<Crystal> crystals = List.of(
					Crystal
							.builder()
							.color("red")
							.id(1L)
							.build(),
					Crystal
							.builder()
							.color("blue")
							.id(2L)
							.build()
			);
			return CrystalsResponse
					.builder()
					.crystals(crystals)
					.amount(price.multiply(BigDecimal.valueOf(amount)))
					.build();
		}
		throw new RuntimeException("Ooups, can't work any more");
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	private static class FringResponse {
		private String name;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	private static class FringRequest {
		private Integer amount;
	}
}
