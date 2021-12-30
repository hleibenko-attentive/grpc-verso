package io.github.heldev.verso.grpc.processor.prototranslation;

import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;

@Value.Immutable
public interface TranslatorViewModel {
	static Builder builder() {
		return ImmutableTranslatorViewModel.builder();
	}

	TypeMirror clazz();
	String method();

	interface Builder {
		Builder clazz(TypeMirror clazz);
		Builder method(String method);

		TranslatorViewModel build();
	}
}
