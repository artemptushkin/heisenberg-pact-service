package io.artemptushkin.github.heisenbergpactservice.controller;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrystalsResponse {
	private BigDecimal amount;
	private List<Crystal> crystals;
}
