package io.github.heldev.verso.grpc.processor.prototranslation.field;

import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;

@Value.Immutable
public interface TranslatorFieldSource extends FieldSource {
	static Builder builder() {
		return ImmutableTranslatorFieldSource.builder();
	}

	TypeMirror translatorClass();
	String method();
	FieldSource underlyingSource();

	interface Builder {
		Builder translatorClass(TypeMirror translatorClass);
		Builder method(String method);
		Builder underlyingSource(FieldSource underlyingSource);

		TranslatorFieldSource build();
	}
}
