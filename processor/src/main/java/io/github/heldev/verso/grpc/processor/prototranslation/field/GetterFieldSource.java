package io.github.heldev.verso.grpc.processor.prototranslation.field;

import org.immutables.value.Value;

@Value.Immutable
public abstract class GetterFieldSource implements FieldSource {

	public static GetterFieldSource of(String getterName) {
		return ImmutableGetterFieldSource.builder()
				.name(getterName)
				.build();
	}

	public abstract String name();
}
