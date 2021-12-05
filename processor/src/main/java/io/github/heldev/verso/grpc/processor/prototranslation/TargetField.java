package io.github.heldev.verso.grpc.processor.prototranslation;


import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;

@Value.Immutable
public interface TargetField {
	static Builder builder() {
		return ImmutableTargetField.builder();
	}

	String getter();
	TypeMirror type();
	String protobufGetter();


	@Value.Check
	default void check() {
		if (getter().trim().isEmpty()) {
			throw new RuntimeException(this + " has an empty proto message");
		}
	}


	interface Builder {
		Builder getter(String name);
		Builder type(TypeMirror type);
		Builder protobufGetter(String protobufGetter);
		TargetField build();
	}
}
