package io.artemptushkin.github.heisenbergpactservice.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/crystals")
@RequiredArgsConstructor
public class CrystalController {

	@GetMapping
	public CrystalsResponse get() {
		return null;
	}
}
