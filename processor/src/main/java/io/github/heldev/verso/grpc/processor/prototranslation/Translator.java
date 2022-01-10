package io.github.heldev.verso.grpc.processor.prototranslation;

import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;

@Value.Immutable
public interface Translator {
	static Builder builder() {
		return ImmutableTranslator.builder();
	}

	TypeMirror location();
	String method();
	TypeMirror source();
	TypeMirror target();

	interface Builder {
		Builder location(TypeMirror location);
		Builder method(String method);
		Builder source(TypeMirror from);
		Builder target(TypeMirror to);

		Translator build();
	}
}
