package io.github.heldev.verso.grpc.processor;

import io.github.heldev.verso.grpc.processor.prototranslation.Translator;
import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Optional;

import static io.github.heldev.verso.grpc.processor.common.Utils.panic;
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
				.collect(collectingAndThen(toList(), matches -> ensureAtMostOne(matches, from, to)));
	}

	private boolean matchesTypes(
			Types typeUtils,
			TypeMirror from,
			TypeMirror to,
			Translator translator) {
		return typeUtils.isSameType(translator.source(), from)
				&& typeUtils.isSameType(translator.target(), to);
	}

	private Optional<Translator> ensureAtMostOne(
			List<Translator> matches,
			TypeMirror from,
			TypeMirror to) {
		if (matches.size() == 1) {
			return matches.stream().findFirst();
		} else {
			throw panic(
					"there should be exactly one translator from %s to %s but found %s",
					from,
					to,
					matches);
		}
	}
}
