package io.github.heldev.verso.grpc.app;


import com.google.protobuf.Timestamp;
import io.github.heldev.verso.grpc.interfaces.VersoCustomGenerator;
import io.github.heldev.verso.grpc.interfaces.VersoCustomTranslator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public abstract class Customizations {

	private Customizations() {}

	@VersoCustomTranslator
	public static UUID stringToUuid(String source) {
		return UUID.fromString(source);
	}

	@VersoCustomTranslator
	public static LocalDate stringToLocalDate(String isoDateTime) {
		return LocalDateTime.parse(isoDateTime).toLocalDate();
	}

	@VersoCustomTranslator
	public static LocalTime stringToLocalTime(String isoDateTime) {
		return LocalDateTime.parse(isoDateTime).toLocalTime();
	}

	@VersoCustomTranslator
	public static Instant timestampToInstant(Timestamp timestamp) {
		return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
	}

	public static final String HARDCODED_UUID = "HARDCODED_UUID";

	@VersoCustomGenerator(HARDCODED_UUID)
	public static UUID hardcodedUuid() {
		return UUID.fromString("0-0-0-0-1");
	}
}
