package io.github.heldev.verso.grpc.processor.prototranslation.field;

import io.github.heldev.verso.grpc.processor.prototranslation.Translator;
import org.immutables.value.Value;

@Value.Immutable
public interface TranslatorFieldSource extends FieldSource {
	static Builder builder() {
		return ImmutableTranslatorFieldSource.builder();
	}

	Translator translator();
	FieldSource underlyingSource();

	interface Builder {
		Builder translator(Translator translator);
		Builder underlyingSource(FieldSource underlyingSource);

		TranslatorFieldSource build();
	}
}
