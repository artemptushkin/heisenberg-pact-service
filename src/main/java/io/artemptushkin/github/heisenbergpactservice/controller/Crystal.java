package io.artemptushkin.github.heisenbergpactservice.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Crystal {
	private Long id;
	private String color;
}
