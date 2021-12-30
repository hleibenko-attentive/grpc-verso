package io.github.heldev.verso.grpc.processor.common;

import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;

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

	TypeMirror type();

	interface Builder {
		Builder id(int id);
		Builder name(String name);
		Builder isOptional(boolean isOptional);
		Builder type(TypeMirror type);

		MessageField build();
	}
}
