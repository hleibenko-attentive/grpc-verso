package io.github.heldev.verso.grpc.processor;

import io.github.heldev.verso.grpc.processor.prototranslation.Translator;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
public abstract class TranslatorCatalog {
	public static TranslatorCatalog of(Iterable<Translator> translators) {
		return ImmutableTranslatorCatalog.builder()
				.translators(translators)
				.build();
	}

	protected abstract List<Translator> translators();

	public Optional<Translator> findTranslator() {
		return translators().stream()
				.filter(translator -> translator.method().equals("stringToUuid"))
				.findAny();
	}
}
