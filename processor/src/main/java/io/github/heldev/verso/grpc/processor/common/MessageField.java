package io.github.heldev.verso.grpc.processor.common;

import org.immutables.value.Value;

@Value.Immutable
public interface MessageField {
	static Builder builder() {
		return ImmutableMessageField.builder();
	}

	int id();

	String name();

	@Value.Default
	default boolean isOptional() {
		return false;
	}

	String type();

	interface Builder {
		Builder id(int id);

		Builder name(String name);

		Builder isOptional(boolean isOptional);

		Builder type(String type);

		MessageField build();
	}
}
