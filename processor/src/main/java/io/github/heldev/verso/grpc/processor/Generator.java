package io.github.heldev.verso.grpc.processor;

import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;

@Value.Immutable
public interface Generator {
	static Builder builder() {
		return ImmutableGenerator.builder();
	}

	TypeMirror location();
	String method();
	String name();

	interface Builder {
		Builder location(TypeMirror location);
		Builder method(String method);
		Builder name(String name);

		Generator build();
	}
}
