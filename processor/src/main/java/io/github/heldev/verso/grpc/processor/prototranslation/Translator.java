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
	TypeMirror from();
	TypeMirror to();

	interface Builder {
		Builder location(TypeMirror location);
		Builder method(String method);
		Builder from(TypeMirror from);
		Builder to(TypeMirror to);

		Translator build();
	}
}
