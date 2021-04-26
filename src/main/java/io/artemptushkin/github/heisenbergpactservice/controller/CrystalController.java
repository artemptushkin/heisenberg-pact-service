package io.artemptushkin.github.heisenbergpactservice.controller;

import java.math.BigDecimal;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/crystals")
@RequiredArgsConstructor
public class CrystalController {

	@GetMapping
	public CrystalsResponse get(@RequestParam Integer amount) {
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
}
