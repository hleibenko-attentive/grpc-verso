package io.github.heldev.verso.grpc.app;


import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import io.github.heldev.verso.grpc.interfaces.VersoField;
import io.github.heldev.verso.grpc.interfaces.VersoFieldTranslator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
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

	@VersoFieldTranslator
	public static Instant timestampToInstant(Timestamp timestamp) {
		return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
	}
}
