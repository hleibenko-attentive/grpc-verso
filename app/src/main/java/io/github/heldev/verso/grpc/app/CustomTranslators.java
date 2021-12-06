package io.github.heldev.verso.grpc.app;


import io.github.heldev.verso.grpc.interfaces.VersoFieldTranslator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public abstract class CustomTranslators {
	private CustomTranslators() {}

	@VersoFieldTranslator
	public static UUID stringToUuid(String source) {
		return UUID.fromString(source);
	}

	@VersoFieldTranslator
	public static LocalDate stringToLocalDate(String isoDateTime) {
		return LocalDateTime.parse(isoDateTime).toLocalDate();
	}

	@VersoFieldTranslator
	public static LocalTime stringToLocalTime(String isoDateTime) {
		return LocalDateTime.parse(isoDateTime).toLocalTime();
	}
}
