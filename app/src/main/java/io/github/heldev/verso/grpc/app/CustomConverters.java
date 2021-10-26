package io.github.heldev.verso.grpc.app;


import io.github.heldev.verso.grpc.interfaces.VersoConverter;

import java.util.UUID;

public abstract class CustomConverters {
	private CustomConverters() {}

	@VersoConverter
	public static UUID stringToUuid(String source) {
		return UUID.fromString(source);
	}
}
