package io.github.heldev.verso.grpc.processor;

import io.github.heldev.verso.grpc.processor.prototranslation.Translator;
import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Value.Immutable
public abstract class TranslatorCatalog {
	public static TranslatorCatalog of(Iterable<Translator> translators) {
		return ImmutableTranslatorCatalog.builder()
				.translators(translators)
				.build();
	}

	protected abstract List<Translator> translators();

	public Optional<Translator> findTranslator(Types typeUtils, TypeMirror from, TypeMirror to) {
		return translators().stream()
				.filter(translator -> matchesTypes(typeUtils, from, to, translator))
				.collect(collectingAndThen(toList(), this::ensureAtMostOne));
	}

	private boolean matchesTypes(
			Types typeUtils,
			TypeMirror from,
			TypeMirror to,
			Translator translator) {
		return typeUtils.isSameType(translator.from(), from)
				&& typeUtils.isSameType(translator.to(), to);
	}

	private Optional<Translator> ensureAtMostOne(List<Translator> matches) {
		if (matches.size() == 1) {
			return matches.stream().findFirst();
		} else {
			throw new RuntimeException("there should be exactly one matching translator but found " + matches);
		}
	}
}
