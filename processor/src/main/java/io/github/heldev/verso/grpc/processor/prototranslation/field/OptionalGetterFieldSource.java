package io.github.heldev.verso.grpc.processor.prototranslation.field;

import org.immutables.value.Value;

@Value.Immutable
public abstract class OptionalGetterFieldSource implements FieldSource {

	public static OptionalGetterFieldSource of(String presenceCheckerName, GetterFieldSource getterFieldSource) {
		return ImmutableOptionalGetterFieldSource.builder()
				.presenceCheckerName(presenceCheckerName)
				.getterFieldSource(getterFieldSource)
				.build();
	}

	public abstract String presenceCheckerName();
	public abstract GetterFieldSource getterFieldSource();
}
