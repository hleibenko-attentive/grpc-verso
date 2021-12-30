package io.github.heldev.verso.grpc.processor.prototranslation;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface AttributeViewModel {
	static Builder builder() {
		return ImmutableAttributeViewModel.builder();
	}

	String builderSetter();
	String sourceGetter();
	Optional<String> sourcePresenceCheckingMethod();
	Optional<TranslatorViewModel> translator();

	interface Builder {
		Builder builderSetter(String setter);
		Builder sourceGetter(String getter);
		Builder sourcePresenceCheckingMethod(String method);
		Builder sourcePresenceCheckingMethod(
				@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<String> method);

		Builder translator(TranslatorViewModel translator);

		Builder translator(
				@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<? extends TranslatorViewModel> translator);

		AttributeViewModel build();
	}
}
