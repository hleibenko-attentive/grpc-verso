package io.github.heldev.verso.grpc.processor.prototranslation;


import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;

@Value.Immutable
public interface TargetType {
	static Builder builder() {
		return ImmutableTargetType.builder();
	}

	TypeMirror type();
	String protoMessage();

	@Value.Check
	default void check() {
		if (protoMessage().trim().isEmpty()) {
			throw new RuntimeException(this + " has an empty proto message");
		}
	}

	interface Builder {
		Builder type(TypeMirror type);
		Builder protoMessage(String protoMessage);
		TargetType build();
	}
}
