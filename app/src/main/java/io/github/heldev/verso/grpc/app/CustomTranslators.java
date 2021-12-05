package io.github.heldev.verso.grpc.app;


import io.github.heldev.verso.grpc.interfaces.VersoFieldTranslator;

import java.util.UUID;

public abstract class CustomTranslators {
	private CustomTranslators() {}

	@VersoFieldTranslator
	public static UUID stringToUuid(String source) {
		return UUID.fromString(source);
	}
}
